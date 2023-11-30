package com.bci.challenge.service.impl;

import com.bci.challenge.dto.*;
import com.bci.challenge.exception.TestAuthenticationException;
import com.bci.challenge.exception.UnauthorizedException;
import com.bci.challenge.exception.UserExistsException;
import com.bci.challenge.exception.UserNotFoundException;
import com.bci.challenge.model.User;
import com.bci.challenge.repository.UserRepository;
import com.bci.challenge.security.JwtTokenUtil;
import com.bci.challenge.security.PasswordEncryptionUtil;
import com.bci.challenge.security.UserDetailsServiceImpl;
import com.bci.challenge.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncryptionUtil passwordEncryptionUtil;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testSignUp_Success() {
        // Mock data
        UserRequest userRequest = mockUserRequest();

        // Mock userRepository methods
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncryptionUtil.encryptPassword(anyString())).thenReturn("encryptedPassword");
        when(jwtTokenUtil.generateToken(anyString())).thenReturn("token");
        when(userRepository.save(any(User.class))).thenReturn(mockUser());

        // Call the service method
        SignUpResponse response = userService.signUp(userRequest);

        // Assertions
        assertNotNull(response);
    }



    @Test
    public void testSignUp_InvalidEmailFormat() {
        // Mock data with an invalid email format
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("invalid_email_format");

        // Mocked dependencies
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncryptionUtil passwordEncryptionUtil = mock(PasswordEncryptionUtil.class);
        JwtTokenUtil jwtTokenUtil = mock(JwtTokenUtil.class);
        UserDetailsServiceImpl userDetailsService = mock(UserDetailsServiceImpl.class);
        AuthenticationService authenticationService = mock(AuthenticationService.class);
        // Mock the UserService
        UserServiceImpl userService = new UserServiceImpl(userRepository, passwordEncryptionUtil,
                jwtTokenUtil, userDetailsService, authenticationService);

        // Test and assert exception for invalid email format
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.signUp(userRequest));
        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void testSignUp_UserExists() {
        // Mock data with an existing email
        UserRequest userRequest = new UserRequest();
        userRequest.setName("John Doe");
        userRequest.setEmail("johndoe@example.com");
        userRequest.setPassword("a2asfGfdfdf4");

        // Mocked dependencies and repository behavior
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));

        // Mocked dependencies
        PasswordEncryptionUtil passwordEncryptionUtil = mock(PasswordEncryptionUtil.class);
        JwtTokenUtil jwtTokenUtil = mock(JwtTokenUtil.class);
        UserDetailsServiceImpl userDetailsService = mock(UserDetailsServiceImpl.class);
        AuthenticationService authenticationService = mock(AuthenticationService.class);
        // Mock the UserService
        UserServiceImpl userService = new UserServiceImpl(userRepository, passwordEncryptionUtil,
                jwtTokenUtil, userDetailsService, authenticationService);
        // Test and assert exception for existing user
        UserExistsException exception = assertThrows(UserExistsException.class, () -> userService.signUp(userRequest));
        assertEquals("User with this email already exists", exception.getMessage());
    }

    @Test
    public void testSignUp_InvalidPasswordPattern() {
        // Mock data with an invalid password pattern
        UserRequest userRequest = new UserRequest();
        userRequest.setName("John Doe");
        userRequest.setEmail("johndoe@example.com");
        userRequest.setPassword("invalidPassword");

        // Call the service method and assert for an exception or error response
        try {
            userService.signUp(userRequest);
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException ex) {
            assertEquals("Invalid password format", ex.getMessage());
        }
    }

    @Test
    public void testAuthenticateAndGenerateToken_Success() {
        // Mock data
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("johndoe@example.com");
        authRequest.setPassword("a2asfGfdfdf4");

        User user = mockUser();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails());
        when(jwtTokenUtil.generateToken(anyString())).thenReturn("token");
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(authenticationService).authenticate(anyString(), anyString());

        // Call the service method
        AuthResponse response = userService.authenticateAndGenerateToken(authRequest);

        // Assertions
        assertNotNull(response);
    }

    @Test
    public void testAuthenticateAndGenerateToken_UserNotFound() {
        // Mock data
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("nonexistent@example.com");
        authRequest.setPassword("somePassword");

        // Mock userRepository methods
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Assertion for UserNotFoundException
        assertThrows(UserNotFoundException.class, () -> userService.authenticateAndGenerateToken(authRequest));
    }

    @Test
    public void testAuthenticateAndGenerateToken_InvalidCredentials() {
        // Mock data
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("existing@example.com");
        authRequest.setPassword("wrongPassword");

        // Mock userRepository methods
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser())); // Provide a mock user for an existing email

        // Mock authenticationService behavior to throw an AuthenticationException
        doThrow(new TestAuthenticationException("Invalid credentials")).when(authenticationService).authenticate(anyString(), anyString());
        // Assertion for UnauthorizedException
        assertThrows(UnauthorizedException.class, () -> userService.authenticateAndGenerateToken(authRequest));
    }

    private User mockUser() {
        User user = new User();
        user.setId(UUID.fromString("e5c6cf84-8860-4c00-91cd-22d3be28904e"));
        user.setEmail("johndoe@example.com");
        user.setPassword("a2asfGfdfdf4");
        user.setCreated(LocalDateTime.parse("2023-11-29T12:51:43"));
        return user;
    }

    private static UserRequest mockUserRequest() {
        UserRequest userRequest = new UserRequest();
        userRequest.setName("John Doe");
        userRequest.setEmail("johndoe@example.com");
        userRequest.setPassword("a2asfGfdfdf4");

        List<PhoneDto> phones = new ArrayList<>();
        // Add some phone data
        PhoneDto phone1 = new PhoneDto();
        phone1.setNumber(12347890);
        phone1.setCityCode(11);
        phone1.setCountryCode("+54");
        phones.add(phone1);

        PhoneDto phone2 = new PhoneDto();
        phone2.setNumber(98763210);
        phone2.setCityCode(354);
        phone2.setCountryCode("+54");
        phones.add(phone2);

        userRequest.setPhones(phones);
        return userRequest;
    }

    private UserDetails mockUserDetails() {
        return new org.springframework.security.core.userdetails.User("johndoe@example.com", "a2asfGfdfdf4", Collections.emptyList());
    }
}