package com.example.server.repositories;

import com.example.server.entities.BlacklistRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistRefreshTokenRepository extends JpaRepository<BlacklistRefreshToken, Long> {
    boolean existsByJti(String jti);
}
