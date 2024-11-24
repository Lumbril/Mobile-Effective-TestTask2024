package com.example.server.services;

import com.example.server.dto.response.TaskWithCommentPaginationResponse;

public interface TaskWithCommentService {
    TaskWithCommentPaginationResponse getWithFilter(Long authorId, Long performerId, int page, int size);
}
