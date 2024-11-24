package com.example.server.dto.request;

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
public class ClientLoginRequest {
    @NotBlank
    @JsonProperty(value = "email", required = true)
    private String email;

    @NotBlank
    @JsonProperty(value = "password", required = true)
    private String password;
}
