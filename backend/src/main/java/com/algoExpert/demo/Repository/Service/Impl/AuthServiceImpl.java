package com.algoExpert.demo.Repository.Service.Impl;

import com.algoExpert.demo.AppNotification.AppEmailBuilder;
import com.algoExpert.demo.AppNotification.EmailHtmlLayout;
import com.algoExpert.demo.AuthService.UserDetailsServiceImpl;
import com.algoExpert.demo.AppUtils.ImageConvertor;
import com.algoExpert.demo.ExceptionHandler.UserAlreadyEnabled;
import com.algoExpert.demo.OAuth2.LoginProvider;
import com.algoExpert.demo.Records.AuthRequest;
import com.algoExpert.demo.Records.RegistrationRequest;
import com.algoExpert.demo.Dto.UserDto;
import com.algoExpert.demo.Entity.HttpResponse;
import com.algoExpert.demo.Entity.User;
import com.algoExpert.demo.ExceptionHandler.InvalidArgument;
import com.algoExpert.demo.Jwt.JwtService;
import com.algoExpert.demo.Repository.Service.AuthService;
import com.algoExpert.demo.Repository.UserRepository;
import com.algoExpert.demo.UserAccount.AccountInfo.Entity.AccountConfirmation;
import com.algoExpert.demo.UserAccount.AccountInfo.Entity.PasswordReset;
import com.algoExpert.demo.UserAccount.AccountInfo.Repository.AccountConfirmationRepository;
import com.algoExpert.demo.UserAccount.AccountInfo.Repository.PasswordResetRepository;
import com.algoExpert.demo.UserAccount.AccountInfo.Service.AccountConfirmationService;
import com.algoExpert.demo.role.Role;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.algoExpert.demo.AppUtils.AppConstants.USERNAME_ALREADY_EXIST;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private AccountConfirmationService confirmationService;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private AppEmailBuilder appEmailBuilder;
    @Autowired
    private EmailHtmlLayout emailHtmlLayout;
    @Value("${confirm.account.url}")
    String confirmLink;

    @Autowired
    private RefreshTokenSevice refreshTokenSevice;
    @Autowired
    private ImageConvertor imageConvertor;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordResetRepository passwordResetRepository;




//    @Autowired
//    UserMapper userMapper;

    //  create user
//    public UserDto create(UserDto userDto) {
////        User user = UserMapper.mapToUser(userDto);
//        User userResults = userRepository.save(user);
////        return UserMapper.mapToUserDto(userResults);
//    }

    @Override
    public User register(User user) {
         user.setPassword(passwordEncoder.encode(user.getPassword()));
         List <Role>  roleList =  user.getRoles();
         roleList.add(Role.valueOf("USER"));
         user.setRoles(roleList);
         return userRepository.save(user);
    }

    /**
     * Register a new user or verifies unactivated account or user.
     *  <p>
     * This method takes fullName,userName and password through request parameter to generate a new user.
     * Initially it validates if the user exist and is enabled,if true,then <b><i>username already exist message</i></b> is displayed from InvalidArgument Exception.
     * Or else if the user is present but not enabled,an email will be sent to a user to verify the account to be enabled to the system.
     * And if the user is not present,a new user will be created and email to verify the account will be sent to the user.
     * @apiNote This method is the implementation of AuthService interface.
     * <strong>@Transactional</strong> ensures data persistence since the user needs to be saved into a database
     * while the email is also been sent, <strong><b>it's all or nothing</b></strong>
     * @see RegistrationRequest
     * @see AuthService
     * @author Santos Rafaelo
     * @param request
     * @return User
     * @throws InvalidArgument if the user already registered in the system
     */

    @Transactional
    @Override
    public User registerUser(RegistrationRequest request) throws InvalidArgument, MessagingException, IOException {
        Optional<User> existingUser = userRepository.findByEmail(request.email());
        StringBuilder link = new StringBuilder(confirmLink);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.isEnabled()) {
                throw new UserAlreadyEnabled(String.format(USERNAME_ALREADY_EXIST, user.getEmail()));
            } else {
                log.info("New token is generated: {}", link);
                AccountConfirmation confirmation = confirmationService.findAccountByUserId(user.getUser_id());
                String token = UUID.randomUUID().toString();

                confirmation.setToken(token);
                confirmation.setCreatedAt(LocalDateTime.now());
                confirmation.setExpiresAt(LocalDateTime.now().plusMinutes(1L));
                confirmationService.saveToken(confirmation);

                log.info("delete method called: {}{}", link, user.getUser_id());
                log.info("Renewed token is generated: {}{}", link, confirmation.getToken());

                //todo change TEMP_USER_EMAIL to request.email()
                String htmlBody = emailHtmlLayout.confirmAccountHtml(user.getFullName(), link.append(token).toString());
                appEmailBuilder.sendEmailAccountConfirmation(request.email(), htmlBody);

                return user;
            }
        } else {
            List<Role> roleList;
            if (request.email().equalsIgnoreCase("admin@gmail.com")) {
                roleList = List.of(Role.ADMIN);
            } else {
                roleList = List.of(Role.USER);
            }

            User user = User.builder()
                    .fullName(request.fullName())
                    .email(request.email())
                    .password(passwordEncoder.encode(request.password()))
                    .roles(roleList)
                    .username(request.email())
                    .provider(LoginProvider.APP)
                    .created_at(LocalDateTime.now())
                    .build();
            User savedUser = userRepository.save(user);
            String token = confirmationService.createToken(user);
            //todo change TEMP_USER_EMAIL to user.getEmail()
            log.info("New Token: {}{}", link, token);

            String htmlBody = emailHtmlLayout.confirmAccountHtml(user.getFullName(), link.append(token).toString());
            appEmailBuilder.sendEmailAccountConfirmation(user.getEmail(), htmlBody);

            return savedUser;
        }
    }


    @Override
    public HttpResponse loginUser(AuthRequest request) {

            Authentication auth =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
            User authenticatedUser = null;
            String jwtToken = "";
            String refreshToken = "";
            List<Role> roles = null;


        if(auth.isAuthenticated()){
            authenticatedUser = (User)auth.getPrincipal();
            jwtToken = jwtService.generateToken(request.email());
            refreshToken = refreshTokenSevice.createRefreshToken(request.email()).getToken();
            roles = authenticatedUser.getRoles();
        }

        return HttpResponse.builder()
                .timeStamp(LocalTime.now().toString())
                .status(HttpStatus.OK)
                .message("Login Successful")
                .email(Objects.requireNonNull(authenticatedUser).getEmail())
                .token(jwtToken)
                .refreshToken(refreshToken)
                .role(roles)
                .fullname(authenticatedUser.getFullName())
                .statusCode(HttpStatus.OK.value())
                .build();

    }

    @Override
    public HttpResponse loginSocialUser(String username) {
        try {
//            Authentication auth =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            User loggedUser = null;
            String jwtToken = "";
            String refreshToken = "";

            if(auth != null  && auth.isAuthenticated()){
                loggedUser = (User) auth.getPrincipal();
                jwtToken = jwtService.generateToken(username);
                refreshToken = refreshTokenSevice.createRefreshToken2(username).getToken();
            }

            return HttpResponse.builder()
                    .timeStamp(LocalTime.now().toString())
                    .status(HttpStatus.OK)
                    .message("Login Successful")
                    .email(loggedUser.getUsername())
                    .token(jwtToken)
                    .refreshToken(refreshToken)
                    .fullname(loggedUser.getFullName())
                    .statusCode(HttpStatus.OK.value())
                    .build();
        } catch (BadCredentialsException e) {
            return HttpResponse.builder()
                    .timeStamp(LocalTime.now().toString())
                    .message("Incorrect username or password")
                    .build();
        } catch (Exception e) {
            return HttpResponse.builder()
                    .timeStamp(LocalTime.now().toString())
                    .message("Login failed: " + e.getMessage())
                    .status(HttpStatus.UNAUTHORIZED)
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .build();
        }
    }

    // get all users
    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    //  delete user by id
    @Override
    public List<UserDto> deleteUser(int userId) {
        userRepository.deleteById(userId);
        return userRepository.findAll()
                .stream().map(user -> new UserDto(user.getUser_id(),
                        user.getUsername(),
                        user.getEmail())).collect(Collectors.toList());
    }

    @Override
    public String forgotPasswordChange(String token, String newPassword) {
        try {
            PasswordReset passwordResetUser =  passwordResetRepository.findByPasswordToken(token).get();

            if(passwordResetUser != null) {
                User user = passwordResetUser.getUser();
                String encodedPassword =  passwordEncoder.encode(newPassword);
                user.setPassword(encodedPassword);
                userRepository.save(user);
                return "password successfully saved";
            }else {
                return "password could not be saved";
            }
        }catch (NoSuchElementException e){
            return "Invalid token";
        }catch (Exception e){
            e.printStackTrace();
            return "error occurred while resetting password";
        }
    }

//    public List<User> deleteUser(int userId){
//        userRepository.deleteById(userId);
//        return userRepository.findAll();
//    }


}
