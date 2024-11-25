package com.example.server.services.impl;

import com.example.server.dto.request.CommentCreateRequest;
import com.example.server.entities.Client;
import com.example.server.entities.Comment;
import com.example.server.entities.Task;
import com.example.server.repositories.CommentRepository;
import com.example.server.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final TaskServiceImpl taskService;

    @Override
    @Transactional
    public Comment createFromRequest(CommentCreateRequest commentCreateRequest, Client author, Long taskId) {
        Task task = taskService.get(taskId);

        Comment comment = Comment.builder()
                .text(commentCreateRequest.getText())
                .author(author)
                .task(task)
                .build();

        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment createFromRequestByPerformer(CommentCreateRequest commentCreateRequest, Client performer, Long taskId) {
        Task task = taskService.getByIdAndPerformerId(taskId, performer.getId());

        Comment comment = Comment.builder()
                .text(commentCreateRequest.getText())
                .author(performer)
                .task(task)
                .build();

        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByTask(Long taskId) {
        return commentRepository.getCommentByTask_Id(taskId);
    }
}
