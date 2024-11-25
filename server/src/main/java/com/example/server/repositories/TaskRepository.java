package com.example.server.repositories;

import com.example.server.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByIdAndAuthor_Id(Long id, Long authorId);
    Optional<Task> findByIdAndPerformer_Id(Long id, Long performerId);
    Page<Task> findAllByAuthor_IdAndPerformer_Id(Long authorId, Long performerId, Pageable pageable);
    Page<Task> findAllByAuthor_Id(Long authorId, Pageable pageable);
    Page<Task> findAllByPerformer_Id(Long performerId, Pageable pageable);
}
