package com.example.server.services;

import com.example.server.dto.request.TaskCreateRequest;
import com.example.server.dto.request.TaskUpdateRequest;
import com.example.server.dto.response.TaskWithCommentPaginationResponse;
import com.example.server.entities.Client;
import com.example.server.entities.Task;
import com.example.server.entities.enums.TaskStatus;

import java.util.List;

public interface TaskService {
    Task get(Long id);
    Task getByIdAndAuthorId(Long id, Long authorId);
    List<Task> getAll();
    Task createFromRequestWithAuthor(TaskCreateRequest taskCreateRequest, Client client);
    Task updateFromRequestWithAuthor(Long id, TaskUpdateRequest taskUpdateRequest, Client client);
    Task changeStatus(Long id, Long clientId, TaskStatus status);
    void deleteByIdAndAuthorId(Long id, Long authorId);
}
