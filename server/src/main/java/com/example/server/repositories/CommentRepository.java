package com.example.server.repositories;

import com.example.server.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> getCommentByTask_Id(Long taskId);
}
