package com.example.server.dto.request;

import com.example.server.entities.enums.TaskPriority;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateRequest {
    @NotBlank
    @JsonProperty(value = "title", required = true)
    private String title;

    @NotBlank
    @JsonProperty(value = "description", required = true)
    private String description;

    @NotNull
    @JsonProperty(value = "priority", required = true)
    private TaskPriority priority;

    @JsonProperty(value = "performer_id", required = true)
    private Long performerId;
}
