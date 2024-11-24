package com.example.server.controllers;

import com.example.server.dto.TokenBody;
import com.example.server.dto.response.ClientResponse;
import com.example.server.dto.response.ErrorResponse;
import com.example.server.dto.response.TaskResponse;
import com.example.server.entities.Client;
import com.example.server.entities.Task;
import com.example.server.entities.enums.Role;
import com.example.server.entities.enums.TaskPriority;
import com.example.server.entities.enums.TaskStatus;
import com.example.server.services.impl.ClientServiceImpl;
import com.example.server.services.impl.JwtServiceImpl;
import com.example.server.services.impl.TaskServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTests {
    @MockBean
    private JwtServiceImpl jwtService;

    @MockBean
    private ClientServiceImpl clientService;

    @MockBean
    private TaskServiceImpl taskService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getTaskNotExistsTest() throws Exception {
        TokenBody tokenBody = TokenBody.builder()
                .email("str@str.com")
                .clientId(1L)
                .exp(new Date())
                .iat(new Date())
                .tokenType("access")
                .jti("jti")
                .build();
        Client client = new Client(1L, "str@str.com", "passwd", Role.ROLE_USER);

        Mockito.doReturn(tokenBody).when(jwtService).getTokenBody(any());
        Mockito.doReturn(client).when(clientService).getByIdFromToken(any());
        Mockito.doThrow(new NoSuchElementException("Задачи с таким id нет")).when(taskService).get(anyLong());

        MvcResult result = mockMvc.perform(get("/api/task/1").header("Authorization", "Bearer <Token>"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        ErrorResponse errorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);

        assertEquals("Задачи с таким id нет", errorResponse.getError());
    }

    @Test
    public void getTaskAccessTest() throws Exception {
        TokenBody tokenBody = TokenBody.builder()
                .email("str@str.com")
                .clientId(1L)
                .exp(new Date())
                .iat(new Date())
                .tokenType("access")
                .jti("jti")
                .build();
        Client client = new Client(1L, "str@str.com", "passwd", Role.ROLE_USER);
        Task task = new Task(1L, "title", "description", TaskStatus.WAITING, TaskPriority.LOW, client, client);
        ClientResponse clientResponseRight = ClientResponse.builder()
                .id(client.getId())
                .email(client.getEmail())
                .build();
        TaskResponse taskResponseRight = TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .author(clientResponseRight)
                .performer(clientResponseRight)
                .build();

        Mockito.doReturn(tokenBody).when(jwtService).getTokenBody(any());
        Mockito.doReturn(client).when(clientService).getByIdFromToken(any());
        Mockito.doReturn(task).when(taskService).get(anyLong());

        MvcResult result = mockMvc.perform(get("/api/task/1").header("Authorization", "Bearer <Token>"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        TaskResponse taskResponse = objectMapper.readValue(response.getContentAsString(), TaskResponse.class);

        assertEquals(taskResponseRight, taskResponse);
    }
}
