package com.bci.challenge.security;

import com.bci.challenge.exception.UserNotFoundException;
import com.bci.challenge.model.User;
import com.bci.challenge.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void testLoadUserByUsername_ValidUser() {
        // Mocking a valid user
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword("password");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Load user by username
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Verify
        assertNotNull(userDetails);
        assertEquals(user.getEmail(), userDetails.getUsername());
        // Assuming password encryption isn't applied
        assertEquals("password", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
    }

    @Test
    public void testLoadUserByUsername_InvalidUser() {
        // Mocking an invalid user
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Verify that UserNotFoundException is thrown for an invalid user
        assertThrows(UserNotFoundException.class, () -> userDetailsService.loadUserByUsername(email));
    }
}