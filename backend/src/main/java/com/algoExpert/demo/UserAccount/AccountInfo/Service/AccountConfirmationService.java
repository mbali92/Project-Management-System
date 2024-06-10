package com.algoExpert.demo.UserAccount.AccountInfo.Service;

import com.algoExpert.demo.Entity.User;
import com.algoExpert.demo.ExceptionHandler.InvalidArgument;
import com.algoExpert.demo.UserAccount.AccountInfo.Entity.AccountConfirmation;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public interface AccountConfirmationService {

    String activateAccount(String token) throws InvalidArgument;

    public String deactivateAccount(Integer userId);

    public String activateAccount(Integer userId);
    void saveToken(AccountConfirmation confirmation);
     String createToken(User user);
    void deleteToken(AccountConfirmation confirmation);
    boolean isTokenExpired(LocalDateTime expiresAt);
    AccountConfirmation findAccountByUserId(int userId);
}
