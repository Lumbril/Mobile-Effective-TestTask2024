package com.example.server.controllers;

import com.example.server.dto.request.TaskCreateRequest;
import com.example.server.dto.request.TaskUpdateRequest;
import com.example.server.dto.request.TaskUpdateStatusRequest;
import com.example.server.dto.response.ClientResponse;
import com.example.server.dto.response.ErrorResponse;
import com.example.server.dto.response.TaskResponse;
import com.example.server.dto.response.TaskWithCommentPaginationResponse;
import com.example.server.entities.Client;
import com.example.server.entities.Task;
import com.example.server.entities.enums.Role;
import com.example.server.services.impl.TaskServiceImpl;
import com.example.server.services.impl.TaskWithCommentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Tag(name = "Tasks", description = "API for tasks")
@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskServiceImpl taskService;
    private final TaskWithCommentServiceImpl taskWithCommentService;

    @Operation(summary = "Создание задачи")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponse.class)
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
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createTask(@RequestBody @Validated TaskCreateRequest taskCreateRequest,
                                        @AuthenticationPrincipal Client client) {
        Task task = taskService.createFromRequestWithAuthor(taskCreateRequest, client);
        TaskResponse taskResponse = getTaskResponse(task);

        return ResponseEntity.ok().body(taskResponse);
    }

    @Operation(summary = "Обновить задачу")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponse.class)
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
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateTask(@PathVariable Long id,
                                        @RequestBody @Validated TaskUpdateRequest taskUpdateRequest,
                                        @AuthenticationPrincipal Client client) {
        Task task = taskService.updateFromRequestWithAuthor(id, taskUpdateRequest, client);
        TaskResponse taskResponse = getTaskResponse(task);

        return ResponseEntity.ok().body(taskResponse);
    }

    @Operation(summary = "Просмотреть задачу")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponse.class)
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
    @GetMapping("/{id}")
    public ResponseEntity<?> getTask(@PathVariable Long id,
                                     @AuthenticationPrincipal Client client) {
        Task task;

        if (client.getRole() == Role.ROLE_ADMIN) {
             task = taskService.get(id);
        } else {
            task = taskService.getByIdAndPerformerId(id, client.getId());
        }

        TaskResponse taskResponse = getTaskResponse(task);

        return ResponseEntity.ok().body(taskResponse);
    }

    @Operation(summary = "Получить список задач")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class))
                    )
            )
    })
    @GetMapping("/list")
    public ResponseEntity<?> getTaskList(@AuthenticationPrincipal Client client) {
        List<Task> taskList;

        if (client.getRole() == Role.ROLE_ADMIN) {
            taskList  = taskService.getAll();
        } else {
            taskList = taskService.getAllByPerformerId(client.getId());
        }

        List<TaskResponse> taskResponseList = taskList.stream()
                .map(task -> getTaskResponse(task))
                .toList();

        return ResponseEntity.ok().body(taskResponseList);
    }

    @Operation(summary = "Удалить задачу")
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "200"
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)

                    )
            )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTask(@PathVariable Long id,
                                        @AuthenticationPrincipal Client client) {
        taskService.deleteByIdAndAuthorId(id, client.getId());

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Поменять статус задачи")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponse.class)
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
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(@PathVariable Long id,
                                          @RequestBody TaskUpdateStatusRequest taskUpdateStatusRequest,
                                          @AuthenticationPrincipal Client client) {
        Task task = taskService.changeStatus(id, client.getId(), taskUpdateStatusRequest.getStatus());
        TaskResponse taskResponse = getTaskResponse(task);

        return ResponseEntity.ok().body(taskResponse);
    }

    @Operation(summary = "Получить задачи по автору или исполнителю",
    description = "Получить отфильтрованные задачи по автору или испольнителю " +
            "со всеми комментариями к ним. Вывод с пагинацией." +
            "Если не указан автор и исполнитель, то выведутся все задачи." +
            "Если указан один из двух, то будет фильтрация по нему." +
            "Если указаны оба, то выведутся все задачи где совпадают оба.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskWithCommentPaginationResponse.class)
                    )
            )
    })
    @GetMapping("/list/filter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getTaskWithFilters(
            @RequestParam(name = "author_id", required = false) Long authorId,
            @RequestParam(name = "performer_id", required = false) Long performerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        TaskWithCommentPaginationResponse response = taskWithCommentService.getWithFilter(authorId, performerId, page, size);

        return ResponseEntity.ok().body(response);
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

    private TaskResponse getTaskResponse(Task task) {
        ClientResponse performer = task.getPerformer() != null ?
                ClientResponse.builder()
                        .id(task.getPerformer().getId())
                        .email(task.getPerformer().getEmail())
                        .build() :
                null;

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .author(ClientResponse.builder()
                        .id(task.getAuthor().getId())
                        .email(task.getAuthor().getEmail())
                        .build())
                .performer(performer)
                .build();
    }
}
