package com.example.demo.service.interfaces;

import java.util.Optional;
import com.example.demo.entity.RefreshToken;

public interface IRefreshTokenService {
    RefreshToken createRefreshToken(String email);

    Optional<RefreshToken> findByToken(String token);

    boolean validateRefreshToken(RefreshToken token);
}