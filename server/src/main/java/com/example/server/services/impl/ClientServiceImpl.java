package com.example.server.services.impl;

import com.example.server.dto.TokenBody;
import com.example.server.dto.request.ClientRegistrationRequest;
import com.example.server.entities.Client;
import com.example.server.entities.enums.Role;
import com.example.server.exceptions.ClientExistsException;
import com.example.server.exceptions.ClientPasswordException;
import com.example.server.repositories.ClientRepository;
import com.example.server.services.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public Client create(ClientRegistrationRequest clientFromRequest) {
        if (!clientFromRequest.getPassword().equals(clientFromRequest.getPasswordConfirm())) {
            throw new ClientPasswordException("Пароли не совпадают");
        }

        if (userIsExists(clientFromRequest.getEmail())) {
            throw new ClientExistsException();
        }

        Client u = Client.builder()
                .email(clientFromRequest.getEmail())
                .password(bCryptPasswordEncoder.encode(clientFromRequest.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        return clientRepository.save(u);
    }

    @Override
    @Transactional(readOnly = true)
    public Client getById(Long id) {
        return clientRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Пользователь с таким id не найден"));
    }

    @Override
    @Transactional(readOnly = true)
    public Client getByEmail(String email) {
        return clientRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("Пользователь с таким email не найден"));
    }

    @Override
    @Transactional(readOnly = true)
    public Client getByIdFromToken(TokenBody tokenBody) {
        return clientRepository.findById(tokenBody.getClientId()).orElseThrow(() -> new NoSuchElementException("Пользователь с таким id не найден"));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        clientRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userIsExists(String email) {
        return clientRepository.findByEmail(email).isPresent();
    }
}
