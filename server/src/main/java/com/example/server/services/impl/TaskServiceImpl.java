package com.example.server.services.impl;

import com.example.server.dto.request.TaskCreateRequest;
import com.example.server.dto.request.TaskUpdateRequest;
import com.example.server.dto.response.ClientResponse;
import com.example.server.dto.response.CommentResponse;
import com.example.server.dto.response.TaskWithCommentPaginationResponse;
import com.example.server.dto.response.TaskWithCommentResponse;
import com.example.server.entities.Client;
import com.example.server.entities.Task;
import com.example.server.entities.enums.TaskStatus;
import com.example.server.repositories.TaskRepository;
import com.example.server.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final ClientServiceImpl clientService;

    @Override
    @Transactional(readOnly = true)
    public Task get(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Задачи с таким id нет"));
    }

    @Override
    @Transactional(readOnly = true)
    public Task getByIdAndAuthorId(Long id, Long authorId) {
        return taskRepository.findByIdAndAuthor_Id(id, authorId)
                .orElseThrow(() -> new NoSuchElementException("Вы не являетесь автором задачи с таким id"));
    }

    @Override
    @Transactional(readOnly = true)
    public Task getByIdAndPerformerId(Long id, Long performerId) {
        return taskRepository.findByIdAndPerformer_Id(id, performerId)
                .orElseThrow(() -> new NoSuchElementException("Вы не являетесь исполнителем задачи с таким id"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getAllByPerformerId(Long performerId) {
        Pageable pageable = PageRequest.of(0, 10);
        return taskRepository.findAllByPerformer_Id(performerId, pageable).getContent();
    }

    @Override
    @Transactional
    public Task createFromRequestWithAuthor(TaskCreateRequest taskCreateRequest, Client client) {
        Client performer = taskCreateRequest.getPerformerId() != null ?
                clientService.getById(taskCreateRequest.getPerformerId()) :
                null;

        Task task = Task.builder()
                .title(taskCreateRequest.getTitle())
                .description(taskCreateRequest.getDescription())
                .status(TaskStatus.WAITING)
                .priority(taskCreateRequest.getPriority())
                .author(client)
                .performer(performer)
                .build();

        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public Task updateFromRequestWithAuthor(Long id, TaskUpdateRequest taskUpdateRequest, Client client) {
        Task task = getByIdAndAuthorId(id, client.getId());
        Client performer = taskUpdateRequest.getPerformerId() != null ?
                clientService.getById(taskUpdateRequest.getPerformerId()) :
                null;

        task.setTitle(taskUpdateRequest.getTitle());
        task.setDescription(taskUpdateRequest.getDescription());
        task.setStatus(taskUpdateRequest.getStatus());
        task.setPriority(taskUpdateRequest.getPriority());
        task.setPerformer(performer);

        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public Task changeStatus(Long id, Long clientId, TaskStatus status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Задачи с таким id нет"));

        if (task.getAuthor().getId() != clientId && task.getPerformer().getId() != clientId) {
            throw new NoSuchElementException("Вы не являетесь автором или исполнителем этой задачи");
        }

        task.setStatus(status);

        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public void deleteByIdAndAuthorId(Long id, Long authorId) {
        Task task = getByIdAndAuthorId(id, authorId);

        taskRepository.delete(task);
    }
}
