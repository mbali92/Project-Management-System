package com.algoExpert.demo.OAuth2;

import com.algoExpert.demo.Entity.User;
import com.algoExpert.demo.Entity.UserNotification;
import com.algoExpert.demo.Repository.Service.Impl.UserNotificationServiceImpl;
import com.algoExpert.demo.Repository.UserRepository;
import com.algoExpert.demo.role.Role;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Data
public class AppUserService implements UserService  {

    @Autowired
    private UserRepository userEntityRepository;

    @Autowired
    private UserNotificationServiceImpl userNotificationService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userEntityRepository
                .findByUsername(username)
                .map(ue -> AppUser
                        .builder()
                        .username(ue.getUsername())
                        .password(ue.getPassword())
                        .fullName(ue.getFullName())
                        .image_url(ue.getImage_url())
                        .provider(ue.getProvider())
                        .authorities(ue.getAuthorities()
                                .stream()
                                .map(a -> new SimpleGrantedAuthority(a.getAuthority()))  // .getName()
                                .toList()
                        )
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("user %s not found ", username)));
    }

    public OAuth2UserService<OidcUserRequest, OidcUser> oidcLoginHandler() {
        return  userRequest -> {
//            login provider
            LoginProvider provider = getProvider(userRequest);
            OidcUserService delegate = new OidcUserService();
            //
            OidcUser oidcUser = delegate.loadUser(userRequest);
//            return google user info user and save to db
            AppUser appUser = AppUser.builder()
                    .provider(provider)
                    .username(oidcUser.getEmail())
                    .fullName(oidcUser.getFullName())
                    .email(oidcUser.getEmail())
                    .user_id(oidcUser.getName())
                    .image_url(oidcUser.getAttribute("picture"))
                    .password(PasswordUtil.encodePassword(UUID.randomUUID().toString()))
                    .attributes(oidcUser.getAttributes())
                    .authorities(oidcUser.getAuthorities())
                    .build();

            // return and save user
            saveOauth2User(appUser);


            return appUser;
        };
    }

    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2LoginHandler() {
        return userRequest -> {
            LoginProvider provider = getProvider(userRequest);
            DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
            OAuth2User oAuth2User = delegate.loadUser(userRequest);
//            return github user info
            AppUser appUser = AppUser
                    .builder()
                    .provider(provider)
                    .username(oAuth2User.getAttribute("login"))
                    .fullName(oAuth2User.getAttribute("login"))
                    .password(PasswordUtil.encodePassword(UUID.randomUUID().toString()))
                    .user_id(oAuth2User.getName())
                    .image_url(oAuth2User.getAttribute("avatar_url"))
                    .attributes(oAuth2User.getAttributes())
                    .authorities(oAuth2User.getAuthorities())
                    .build();

//            return and save user
            saveOauth2User(appUser);

            return appUser;
        };
    }

    //    method to save oauth2 users
    void saveOauth2User(AppUser appUser) {
        createUser(appUser);
    }

//    get login provider from OAuth2 login
    private LoginProvider getProvider(OAuth2UserRequest userRequest) {
        return LoginProvider.valueOf(userRequest
                .getClientRegistration()
                .getRegistrationId().toUpperCase());
    }

    @Transactional
    public void createUser(AppUser user){
//        create user if not exist
        User userEntity = saveUserIfNotExists(user);
        userEntityRepository.save(userEntity);
    }

    //    checks if user exists the save in db if not
    private User saveUserIfNotExists(AppUser user) {
        return userEntityRepository.findByUsername(user.getUsername())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .email(user.getEmail())
                            .fullName(user.getFullName())
                            .image_url(user.getImage_url())
                            .provider(user.getProvider())
                            .dateRegistered(LocalDate.now())
                            .roles(Collections.singletonList(Role.USER))
                            .enabled(true)
                            .build();
                    return userEntityRepository.save(newUser);
                });
    }

    @Override
    public void createUser(UserDetails user) {

    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void updateUser(AppUser user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteUser(String username) {
        if (userExists(username)){
            userEntityRepository.deleteByUsername(username);
        }
    }

    @Override
    @Transactional //because it changes data
    public void changePassword(String oldPassword, String newPassword) {
//        get details of logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
        AppUser currentUser = (AppUser) authentication.getPrincipal();
//  check if password are the same and throw error if yes
//        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())){
        if (!PasswordUtil.matches(oldPassword, currentUser.getPassword())){
            throw new IllegalArgumentException("Old password is same as old password");
        }

//  find user by id and set new password
        userEntityRepository
                .findByUsername(currentUser.getUsername())
                .ifPresent(ue -> ue.setPassword(PasswordUtil.encodePassword(newPassword)));
    }

    @Override
    @Transactional
    public boolean userExists(String username) {
        return userEntityRepository.existsByUsername(username);
    }

    public List<User> getAllUsers() {
        return userEntityRepository.findAll();
    }
}
