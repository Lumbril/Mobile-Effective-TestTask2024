package com.example.server.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskWithCommentPaginationResponse {
    @JsonProperty(value = "tasks")
    private List<TaskWithCommentResponse> tasks;

    @JsonProperty(value = "current_page")
    private long currentPage;

    @JsonProperty(value = "total_items")
    private long totalItems;

    @JsonProperty(value = "total_pages")
    private long totalPages;
}
