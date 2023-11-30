package com.bci.challenge.exception;

import org.springframework.security.core.AuthenticationException;

public class TestAuthenticationException extends AuthenticationException {
    public TestAuthenticationException(String message) {
        super(message);
    }
}