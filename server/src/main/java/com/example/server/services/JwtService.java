package com.example.server.services;

import com.example.server.dto.TokenBody;
import com.example.server.dto.request.ClientLoginRequest;
import com.example.server.dto.request.RefreshRequest;
import com.example.server.dto.response.AccessAndRefreshJwtResponse;

public interface JwtService {
    TokenBody getTokenBody(String token);
    AccessAndRefreshJwtResponse createAccessAndRefreshTokens(ClientLoginRequest userLoginRequest);
    AccessAndRefreshJwtResponse refreshTokens(RefreshRequest refreshRequest);
}
