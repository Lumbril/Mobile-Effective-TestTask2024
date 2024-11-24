package com.example.server.filters;

import com.example.server.dto.TokenBody;
import com.example.server.entities.Client;
import com.example.server.exceptions.InvalidTokenException;
import com.example.server.services.impl.JwtServiceImpl;
import com.example.server.services.impl.ClientServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final ClientServiceImpl clientService;
    private final JwtServiceImpl jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (!(authHeader != null && authHeader.startsWith("Bearer "))) {
            filterChain.doFilter(request, response);

            return;
        }

        TokenBody tokenBody;

        try {
            tokenBody = jwtService.getTokenBody(authHeader.substring(7));
        } catch (InvalidTokenException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            return;
        }

        if (!tokenBody.getTokenType().equals("access")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            return;
        }

        Client client = clientService.getByIdFromToken(tokenBody);

        if (client == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            return;
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(client, null, client.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
