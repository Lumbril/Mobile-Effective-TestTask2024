package com.example.server.dto.request;

import com.example.server.entities.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateStatusRequest {
    @NotBlank
    @JsonProperty(value = "status", required = true)
    private TaskStatus status;
}
