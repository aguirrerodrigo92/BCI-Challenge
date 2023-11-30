package com.bci.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class AuthResponse {
    private UUID id;
    private String created;
    @JsonProperty(value = "last_login")
    private String lastLogin;
    private String token;
    @JsonProperty(value = "is_active")
    private boolean isActive;
    private String name;
    private String email;
    private String password;
    private List<PhoneDto> phones;
}
