package com.algoExpert.demo.Repository.Service;

import com.algoExpert.demo.Entity.HttpResponse;
import com.algoExpert.demo.Entity.RefreshToken;
import org.springframework.stereotype.Service;

@Service
public interface RefreshTokenInt {
    RefreshToken createRefreshToken(String username);
    RefreshToken verifyExpiration(RefreshToken token);
    HttpResponse userLogout();
}
