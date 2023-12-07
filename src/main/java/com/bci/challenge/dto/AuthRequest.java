package com.bci.challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Authentication request object")
public class AuthRequest {
    @Schema(description = "User's email", example = "john@example.com")
    String email;

    @Schema(description = "User's password", example = "a2asfGfdfdf4")
    String password;
}