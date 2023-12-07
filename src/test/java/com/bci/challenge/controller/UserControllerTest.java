package com.bci.challenge.controller;

import com.bci.challenge.dto.*;
import com.bci.challenge.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSignUp_Success() {
        // Mock data
        UserRequest userRequest = new UserRequest();
        // Set userRequest properties

        // Mock your UserService
        UserService userService = mock(UserService.class);

        // Mock the signUp method in UserService
        SignUpResponse mockSignUpResponse = new SignUpResponse(); // Create a mock response
        when(userService.signUp(any(UserRequest.class))).thenReturn(mockSignUpResponse);

        // Create UserController and inject the mocked UserService
        UserController userController = new UserController(userService);

        // Call the signUp method in UserController
        ResponseEntity<SignUpResponse> responseEntity = userController.signUp(userRequest);

        // Assertions
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        SignUpResponse responseBody = responseEntity.getBody();
        assertNotNull(responseBody);

        // Check individual fields of the SignUpResponse
        assertEquals(mockSignUpResponse.getId(), responseBody.getId());
        assertEquals(mockSignUpResponse.getCreated(), responseBody.getCreated());
        assertEquals(mockSignUpResponse.getToken(), responseBody.getToken());
        // Check other fields as needed

        // Check the response body equality
        assertEquals(mockSignUpResponse, responseBody);

        // Verify that the userService.signUp method was called with the correct argument
        verify(userService, times(1)).signUp(userRequest);
    }

    @Test
    public void testLogin_Success() {
        // Mock data
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password");

        AuthResponse mockResponse = mockAuthResponse();

        // Mock the userService behavior
        when(userService.authenticateAndGenerateToken(any(AuthRequest.class))).thenReturn(mockResponse);

        // Call the controller method
        ResponseEntity<?> responseEntity = userController.login(authRequest);

        // Assertions
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Check individual fields of the response object
        AuthResponse responseBody = (AuthResponse) responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(mockResponse.getId(), responseBody.getId());
        assertEquals(mockResponse.getCreated(), responseBody.getCreated());
        assertEquals(mockResponse.getLastLogin(), responseBody.getLastLogin());
        assertEquals(mockResponse.getToken(), responseBody.getToken());
        assertEquals(mockResponse.isActive(), responseBody.isActive());
        assertEquals(mockResponse.getName(), responseBody.getName());
        assertEquals(mockResponse.getEmail(), responseBody.getEmail());
        assertEquals(mockResponse.getPassword(), responseBody.getPassword());
        // Add assertions for the phone details if needed
        assertEquals(mockResponse.getPhones().size(), responseBody.getPhones().size());
        // Check other fields as needed

        // Check the response body equality
        assertEquals(mockResponse, responseBody);
    }

    public static AuthResponse mockAuthResponse() {
        // Adding a phone
        PhoneDto phone = new PhoneDto();
        phone.setNumber(87650009);
        phone.setCityCode(11);
        phone.setCountryCode("+54");

        List<PhoneDto> phones = new ArrayList<>();
        phones.add(phone);

        return AuthResponse.builder()
                .id(UUID.fromString("e5c6cf84-8860-4c00-91cd-22d3be28904e"))
                .created(LocalDateTime.now().toString())
                .lastLogin(LocalDateTime.now().toString())
                .token("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWxpb0B0ZXN0...")
                .isActive(true)
                .name("John Doe")
                .email("johndoe@example.com")
                .password("a2asfGfdfdf4")
                .phones(phones)
                .build();
    }
}