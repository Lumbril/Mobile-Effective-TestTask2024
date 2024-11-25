package com.example.server.services;

import com.example.server.dto.request.CommentCreateRequest;
import com.example.server.entities.Client;
import com.example.server.entities.Comment;

import java.util.List;

public interface CommentService {
    Comment createFromRequest(CommentCreateRequest commentCreateRequest, Client author, Long taskId);
    Comment createFromRequestByPerformer(CommentCreateRequest commentCreateRequest, Client performer, Long taskId);
    List<Comment> getCommentsByTask(Long taskId);
}
