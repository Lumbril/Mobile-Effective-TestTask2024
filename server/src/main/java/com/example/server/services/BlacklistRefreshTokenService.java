package com.example.server.services;

import com.example.server.entities.BlacklistRefreshToken;

public interface BlacklistRefreshTokenService {
    BlacklistRefreshToken save(String jti);
    boolean isExists(String jti);
}
