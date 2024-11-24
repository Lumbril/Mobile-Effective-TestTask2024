package com.example.server.services.impl;

import com.example.server.entities.BlacklistRefreshToken;
import com.example.server.repositories.BlacklistRefreshTokenRepository;
import com.example.server.services.BlacklistRefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlacklistRefreshTokenServiceImpl implements BlacklistRefreshTokenService {
    private final BlacklistRefreshTokenRepository blacklistRefreshTokenRepository;

    @Override
    @Transactional
    public BlacklistRefreshToken save(String jti) {
        return blacklistRefreshTokenRepository.save(
                BlacklistRefreshToken.builder()
                        .jti(jti)
                        .build());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExists(String jti) {
        return blacklistRefreshTokenRepository.existsByJti(jti);
    }
}
