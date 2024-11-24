package com.example.server.services.impl;

import com.example.server.dto.TokenBody;
import com.example.server.dto.request.ClientLoginRequest;
import com.example.server.dto.request.RefreshRequest;
import com.example.server.dto.response.AccessAndRefreshJwtResponse;
import com.example.server.entities.Client;
import com.example.server.exceptions.ClientInvalidDataException;
import com.example.server.exceptions.InvalidTokenException;
import com.example.server.exceptions.ServerErrorException;
import com.example.server.services.JwtService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    @Value("${application.security.jwt.private-key}")
    private String PRIVATE_KEY;
    @Value("${application.security.jwt.public-key}")
    private String PUBLIC_KEY;
    @Value("${application.security.jwt.access-token.expiration}")
    private long ACCESS_TOKEN_EXPIRATION;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long REFRESH_TOKEN_EXPIRATION;

    private final ClientServiceImpl clientService;
    private final BlacklistRefreshTokenServiceImpl blacklistRefreshTokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public TokenBody getTokenBody(String token) {
        Claims claims = getClaimsIfTokenValid(token);

        try {
            return TokenBody.builder()
                    .clientId(claims.get("user_id", Long.class))
                    .email(claims.get("email", String.class))
                    .tokenType(claims.get("token_type", String.class))
                    .jti(claims.getId())
                    .iat(claims.getIssuedAt())
                    .exp(claims.getExpiration())
                    .build();
        } catch (RequiredTypeException exception) {
            throw new InvalidTokenException();
        }
    }

    @Override
    @Transactional
    public AccessAndRefreshJwtResponse createAccessAndRefreshTokens(ClientLoginRequest clientLoginRequest) {
        Client user = getClientIfPasswordCorrect(clientLoginRequest);

        String accessToken = createAccessToken(user);
        String refreshToken = createRefreshToken(user);

        return AccessAndRefreshJwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional
    public AccessAndRefreshJwtResponse refreshTokens(RefreshRequest refreshRequest) {
        TokenBody tokenBody = getTokenBody(refreshRequest.getRefreshToken());

        if (!tokenBody.getTokenType().equals("refresh")) {
            throw new InvalidTokenException();
        }

        if (blacklistRefreshTokenService.isExists(tokenBody.getJti())) {
            throw new InvalidTokenException("This refresh token is invalid");
        }

        Client client = clientService.getById(tokenBody.getClientId());

        String accessToken = createAccessToken(client);
        String refreshToken = createRefreshToken(client);

        blacklistRefreshTokenService.save(tokenBody.getJti());

        return AccessAndRefreshJwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String createAccessToken(Client client) {
        return buildToken(new HashMap<>(), client, "access", ACCESS_TOKEN_EXPIRATION);
    }

    private String createRefreshToken(Client client) {
        return buildToken(new HashMap<>(), client, "refresh", REFRESH_TOKEN_EXPIRATION);
    }

    private String buildToken(Map<String, Object> extraClaims, Client clientDetails, String type, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setHeaderParam("typ", "JWT")
                .claim("user_id", clientDetails.getId())
                .claim("email", clientDetails.getUsername())
                .claim("token_type", type)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getPrivateKey(), SignatureAlgorithm.RS512)
                .compact();
    }

    private Claims getClaimsIfTokenValid(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getPublicKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException |
                 UnsupportedJwtException |
                 MalformedJwtException |
                 SignatureException |
                 IllegalArgumentException exception) {
            throw new InvalidTokenException();
        }
    }

    private Client getClientIfPasswordCorrect(ClientLoginRequest clientLoginRequest) {
        Client client;

        try {
            client = clientService.getByEmail(clientLoginRequest.getEmail());
        } catch (NoSuchElementException e) {
            throw new ClientInvalidDataException();
        }

        if (!bCryptPasswordEncoder.matches(clientLoginRequest.getPassword(), client.getPassword())) {
            throw new ClientInvalidDataException();
        }

        return client;
    }

    private Key getPrivateKey() {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] bytes = Base64.getDecoder().decode(PRIVATE_KEY);
            PKCS8EncodedKeySpec keySpecPv = new PKCS8EncodedKeySpec(bytes);

            return keyFactory.generatePrivate(keySpecPv);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    private Key getPublicKey() {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte [] bytes = Base64.getDecoder().decode(PUBLIC_KEY);
            X509EncodedKeySpec keySpecPv = new X509EncodedKeySpec(bytes);

            return keyFactory.generatePublic(keySpecPv);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new ServerErrorException(e.getMessage());
        }
    }
}
