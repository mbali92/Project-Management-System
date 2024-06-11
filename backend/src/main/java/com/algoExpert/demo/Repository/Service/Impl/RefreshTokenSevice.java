package com.algoExpert.demo.Repository.Service.Impl;

import com.algoExpert.demo.Entity.HttpResponse;
import com.algoExpert.demo.Entity.RefreshToken;
import com.algoExpert.demo.Entity.User;
import com.algoExpert.demo.ExceptionHandler.InvalidArgument;
import com.algoExpert.demo.Repository.RefreshTokenRepository;
import com.algoExpert.demo.Repository.Service.RefreshTokenInt;
import com.algoExpert.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenSevice implements RefreshTokenInt {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

//    method to create refresh token
    public RefreshToken createRefreshToken(String username){
        User user = userRepository.findByUsername(username).orElseThrow(()-> new InvalidArgument("Token not found"));;

        RefreshToken refreshToken =  RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plus(365, ChronoUnit.DAYS))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now())<0){
           refreshTokenRepository.delete(token);
            throw new RuntimeException((token.getToken()+"Refresh token has expired . Please make a new login request"));
        }
        return token;
    }

    @Override
    public HttpResponse userLogout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // Get the principal (authenticated user)
            User loggedUser = (User) authentication.getPrincipal();
            Optional<RefreshToken> refreshTokenUser = refreshTokenRepository.findTokenByUserId(loggedUser.getUser_id());

            if(refreshTokenUser.isPresent()){
                refreshTokenRepository.delete(refreshTokenUser.get());
                return HttpResponse.builder()
                        .status(HttpStatus.OK)
                        .message("user logged out")
                        .build();
            }
        }
        return HttpResponse.builder()
                .message("could not logout")
                .build();
    }
}
