package com.example.server.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientResponse {
    @JsonProperty(value = "id")
    private Long id;

    @JsonProperty(value = "email")
    private String email;
}
