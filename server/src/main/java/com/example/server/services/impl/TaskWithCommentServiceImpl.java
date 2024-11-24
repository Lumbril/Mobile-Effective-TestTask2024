package com.example.server.services.impl;

import com.example.server.dto.response.ClientResponse;
import com.example.server.dto.response.CommentResponse;
import com.example.server.dto.response.TaskWithCommentPaginationResponse;
import com.example.server.dto.response.TaskWithCommentResponse;
import com.example.server.entities.Task;
import com.example.server.repositories.TaskRepository;
import com.example.server.services.TaskWithCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskWithCommentServiceImpl implements TaskWithCommentService {
    private final TaskRepository taskRepository;
    private final CommentServiceImpl commentService;

    @Override
    @Transactional(readOnly = true)
    public TaskWithCommentPaginationResponse getWithFilter(Long authorId, Long performerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> taskPage;

        if (authorId == null && performerId == null) {
            taskPage = taskRepository.findAll(pageable);
        } else if (authorId != null && performerId != null) {
            taskPage = taskRepository.findAllByAuthor_IdAndPerformer_Id(authorId, performerId, pageable);
        } else if (authorId != null) {
            taskPage = taskRepository.findAllByAuthor_Id(authorId, pageable);
        } else {
            taskPage = taskRepository.findAllByPerformer_Id(performerId, pageable);
        }

        List<Task> tasks = taskPage.getContent();

        TaskWithCommentPaginationResponse taskWithCommentPaginationResponse = TaskWithCommentPaginationResponse.builder()
                .tasks(
                        tasks.stream()
                                .map(task -> TaskWithCommentResponse.builder()
                                        .id(task.getId())
                                        .title(task.getTitle())
                                        .description(task.getDescription())
                                        .status(task.getStatus())
                                        .priority(task.getPriority())
                                        .author(ClientResponse.builder()
                                                .id(task.getAuthor().getId())
                                                .email(task.getAuthor().getEmail())
                                                .build())
                                        .performer(ClientResponse.builder()
                                                .id(task.getPerformer().getId())
                                                .email(task.getPerformer().getEmail())
                                                .build())
                                        .comments(commentService.getCommentsByTask(task.getId()).stream()
                                                .map(comment -> CommentResponse.builder()
                                                        .id(comment.getId())
                                                        .text(comment.getText())
                                                        .author(ClientResponse.builder()
                                                                .id(comment.getAuthor().getId())
                                                                .email(comment.getAuthor().getEmail())
                                                                .build())
                                                        .taskId(comment.getTask().getId())
                                                        .build())
                                                .toList())
                                        .build())
                                .toList()
                )
                .currentPage(taskPage.getNumber())
                .totalItems(taskPage.getTotalElements())
                .totalPages(taskPage.getTotalPages())
                .build();

        return taskWithCommentPaginationResponse;
    }
}
