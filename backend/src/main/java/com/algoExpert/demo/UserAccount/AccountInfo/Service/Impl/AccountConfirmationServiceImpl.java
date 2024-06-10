package com.algoExpert.demo.UserAccount.AccountInfo.Service.Impl;

import com.algoExpert.demo.AppNotification.AppEmailBuilder;
import com.algoExpert.demo.AppNotification.EmailHtmlLayout;
import com.algoExpert.demo.Entity.User;
import com.algoExpert.demo.ExceptionHandler.InvalidArgument;
import com.algoExpert.demo.Repository.UserRepository;
import com.algoExpert.demo.UserAccount.AccountInfo.Entity.AccountConfirmation;
import com.algoExpert.demo.UserAccount.AccountInfo.Repository.AccountConfirmationRepository;
import com.algoExpert.demo.UserAccount.AccountInfo.Service.AccountConfirmationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.algoExpert.demo.AppUtils.AppConstants.TOKEN_NOT_FOUND;

@Service
@Slf4j
public class AccountConfirmationServiceImpl implements AccountConfirmationService {

    @Autowired
    private AccountConfirmationRepository confirmationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AppEmailBuilder appEmailBuilder ;
    @Autowired
    private EmailHtmlLayout emailHtmlLayout;
    @Value("${confirm.account.url}")
    String confirmLink;
    private static final Long EXPIRING_TIME = 2L;

    @Override
    public String activateAccount(String userToken)  {
      AccountConfirmation confirmation = confirmationRepository.findByToken(userToken).orElseThrow(()-> new InvalidArgument(String.format(TOKEN_NOT_FOUND,userToken)));

        User user = confirmation.getUser();
       if(user.isEnabled()){
           log.info("renewedToken {}{} :",confirmLink,confirmation.getToken());
           return "Account Already Activated,Click The Button Below To Login.";
       }

       if (isTokenExpired(confirmation.getExpiresAt())){
           deleteToken(confirmation);
           String renewToken = createToken(user);

           log.info("renewedToken {}{} :",confirmLink,renewToken);
           String link = confirmLink + renewToken;

//           appEmailBuilder.sendEmailAccountConfirmation(TEMP_USER_EMAIL,
//                   emailHtmlLayout.buildAccConfirmationEmail(user.getUsername(),link));
           return "Token Expired Please Check Your Email To Confirm!";
       }



       user.setEnabled(true);
       user.setDateRegistered(LocalDate.now());
        userRepository.save(user);
        confirmation.setConfirmAt(LocalDateTime.now());
        saveToken(confirmation);
        return "Account Activated Successful,Click The Button Below To Login.";
    }

    @Override
    public String deactivateAccount(Integer userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.isEnabled()) {
                return "Account is already deactivated.";
            }

            user.setEnabled(false);
            userRepository.save(user);
            return "Account deactivated successfully.";
        } else {
            return "User not found.";
        }
    }

    @Override
    public String activateAccount(Integer userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.isEnabled()) {
                return "Account is already activated.";
            }

            user.setEnabled(true);
            userRepository.save(user);
            return "Account activated successfully.";
        } else {
            return "User not found.";
        }
    }

    @Override
    public void saveToken(AccountConfirmation confirmation) {
        confirmationRepository.save(confirmation);
    }

    @Override
    public void deleteToken(AccountConfirmation confirmation) {
        confirmationRepository.delete(confirmation);
    }

    @Override
    public boolean isTokenExpired(LocalDateTime expiresAt) {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    @Override
    public AccountConfirmation findAccountByUserId(int userId) {
        return confirmationRepository.findByUserId(userId);
    }

    @Override
    public String createToken(User user){
        String token = UUID.randomUUID().toString();

        AccountConfirmation confirmation = AccountConfirmation.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(EXPIRING_TIME))
                .user(user).build();
        saveToken(confirmation);
        return confirmation.getToken();
    }
}
