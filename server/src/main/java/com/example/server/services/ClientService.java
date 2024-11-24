package com.example.server.services;

import com.example.server.dto.TokenBody;
import com.example.server.dto.request.ClientRegistrationRequest;
import com.example.server.entities.Client;

public interface ClientService {
    Client create(ClientRegistrationRequest clientFromRequest);
    Client getById(Long id);
    Client getByEmail(String email);
    Client getByIdFromToken(TokenBody tokenBody);
    void deleteById(Long id);
    boolean userIsExists(String email);
}
