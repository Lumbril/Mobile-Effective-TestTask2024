package com.example.server.controllers;

import com.example.server.dto.request.CommentCreateRequest;
import com.example.server.dto.response.ClientResponse;
import com.example.server.dto.response.CommentResponse;
import com.example.server.dto.response.ErrorResponse;
import com.example.server.dto.response.TaskResponse;
import com.example.server.entities.Client;
import com.example.server.entities.Comment;
import com.example.server.entities.enums.Role;
import com.example.server.services.impl.CommentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@Tag(name = "Comments", description = "API for comments")
@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentServiceImpl commentService;

    @Operation(summary = "Оставить комментарий к задаче")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommentResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/{task_id}")
    public ResponseEntity<?> createComment(@PathVariable(value = "task_id") Long taskId,
                                           @RequestBody CommentCreateRequest commentCreateRequest,
                                           @AuthenticationPrincipal Client client) {
        Comment comment;
        if (client.getRole() == Role.ROLE_ADMIN) {
            comment = commentService.createFromRequest(commentCreateRequest, client, taskId);
        } else {
            comment = commentService.createFromRequestByPerformer(commentCreateRequest, client, taskId);
        }

        CommentResponse commentResponse = getCommentResponse(comment);

        return ResponseEntity.ok().body(commentResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestValue(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .error("Ошибка валидации")
                        .build()
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException e) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .error(e.getMessage())
                        .build()
        );
    }

    private CommentResponse getCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(ClientResponse.builder()
                        .id(comment.getAuthor().getId())
                        .email(comment.getAuthor().getEmail())
                        .build())
                .taskId(comment.getTask().getId())
                .build();
    }
}
