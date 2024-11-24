package com.example.server.dto.response;

import com.example.server.entities.enums.TaskPriority;
import com.example.server.entities.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskResponse {
    @JsonProperty(value = "id")
    private Long id;

    @JsonProperty(value = "title")
    private String title;

    @JsonProperty(value = "description")
    private String description;

    @JsonProperty(value = "status")
    private TaskStatus status;

    @JsonProperty(value = "priority")
    private TaskPriority priority;

    @JsonProperty(value = "author")
    private ClientResponse author;

    @JsonProperty(value = "performer")
    private ClientResponse performer;
}
