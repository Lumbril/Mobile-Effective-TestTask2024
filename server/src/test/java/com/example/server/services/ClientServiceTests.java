package com.example.server.services;

import com.example.server.dto.request.ClientRegistrationRequest;
import com.example.server.entities.Client;
import com.example.server.entities.enums.Role;
import com.example.server.exceptions.ClientExistsException;
import com.example.server.exceptions.ClientPasswordException;
import com.example.server.repositories.ClientRepository;
import com.example.server.services.impl.ClientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTests {
    @Mock
    private ClientRepository clientRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    public void createWithDifferentPasswordTest() {
        ClientRegistrationRequest clientRegistrationRequest = ClientRegistrationRequest.builder()
                .email("stt@str.com")
                .password("12345")
                .passwordConfirm("1234")
                .build();

        ClientPasswordException exception = assertThrows(ClientPasswordException.class,
                () -> clientService.create(clientRegistrationRequest));

        assertEquals("Пароли не совпадают", exception.getMessage());
    }

    @Test
    public void createWhenUserIsExistsTest() {
        ClientRegistrationRequest clientRegistrationRequest = ClientRegistrationRequest.builder()
                .email("str@str.com")
                .password("12345")
                .passwordConfirm("12345")
                .build();
        Client client = new Client(1L, "str@str.com", "passwd", Role.ROLE_USER);
        Mockito.doReturn(Optional.of(client)).when(clientRepository).findByEmail(any());

        ClientExistsException exception = assertThrows(ClientExistsException.class,
                () -> clientService.create(clientRegistrationRequest));

        assertEquals("Пользователь с таким именем уже есть.", exception.getMessage());
    }

    @Test
    public void createUserAccessTest() {
        ClientRegistrationRequest clientRegistrationRequest = ClientRegistrationRequest.builder()
                .email("str@str.com")
                .password("12345")
                .passwordConfirm("12345")
                .build();
        Client client = new Client(1L, "str@str.com", "passwd", Role.ROLE_USER);
        Mockito.doReturn(Optional.empty()).when(clientRepository).findByEmail(any());
        Mockito.doReturn(client).when(clientRepository).save(any());
        Mockito.doReturn("passwd").when(bCryptPasswordEncoder).encode(any());

        Client saveClient = clientService.create(clientRegistrationRequest);
        assertEquals(client, saveClient);
    }
}
