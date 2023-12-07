package com.bci.challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Request DTO for {@link com.bci.challenge.model.User}
 */
@Data
@Schema(description = "User request object")
public class UserRequest implements Serializable {
    @Schema(description = "User's name", example = "John Doe")
    String name;

    @Schema(description = "User's email", example = "john@example.com")
    String email;

    @Schema(description = "User's password", example = "a2asfGfdfdf4")
    String password;

    @Schema(description = "List of user's phones")
    List<PhoneDto> phones;
}
