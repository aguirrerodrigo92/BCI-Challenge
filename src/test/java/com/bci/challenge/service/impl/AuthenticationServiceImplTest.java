package com.bci.challenge.service.impl;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Test
    public void testAuthenticate() {
        // Initialize mocks
        openMocks(this);

        AuthenticationServiceImpl authenticationService = new AuthenticationServiceImpl(authenticationManager);

        // Test data
        String username = "userTest";
        String password = "passwordTest";

        // Call the method under test
        authenticationService.authenticate(username, password);

        // Verify that the authenticate method of the AuthenticationManager is called with the expected parameters
        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}