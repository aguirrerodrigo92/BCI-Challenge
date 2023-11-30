package com.bci.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpResponse {
    private UUID id;
    private LocalDateTime created;
    //private LocalDateTime lastLogin; --> sign-up scope?
    private String token;
    //private boolean isActive; --> sign-up scope?
}
