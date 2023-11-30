package com.bci.challenge.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtTokenUtilTest {

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    private static final String JWT_SECRET_KEY = "secretKey";


    @InjectMocks
    private JwtTokenUtil jwtTokenUtil;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        jwtTokenUtil = new JwtTokenUtil(userDetailsService);
        ReflectionTestUtils.setField(jwtTokenUtil, "JWT_SECRET_KEY", "secretKey");
    }
    @Test
    public void testGenerateToken() {
        String email = "test@example.com";
        String token = jwtTokenUtil.generateToken(email);

        assertNotNull(token);
    }

    @Test
    public void testGetUsernameFromToken() {
        String email = "test@example.com";
        String token = jwtTokenUtil.generateToken(email);

        String username = jwtTokenUtil.getUsernameFromToken(token);

        assertEquals(email, username);
    }

    @Test
    public void testValidateToken_ValidToken() {
        String email = "test@example.com";
        String token = jwtTokenUtil.generateToken(email);

        UserDetails userDetails = User.withUsername(email).password("password").roles("USER").build();

        boolean isValid = jwtTokenUtil.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    public void testValidateToken_ExpiredToken() {
        String email = "test@example.com";

        // Expire the token manually by setting an expiration date in the past
        String expiredToken = Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // Expired token
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET_KEY)
                .compact();

        UserDetails userDetails = User.withUsername(email).password("password").roles("USER").build();

        assertThrows(ExpiredJwtException.class, () -> jwtTokenUtil.validateToken(expiredToken, userDetails));
    }

    @Test
    public void testLoadUserByUsername() {
        // Create a mock UserDetails object
        UserDetails mockUserDetails = new User("test@example.com", "password", Collections.emptyList());

        // Define the behavior of userDetailsService.loadUserByUsername
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(mockUserDetails);

        // Call the method being tested
        UserDetails loadedUser = jwtTokenUtil.loadUserByUsername("test@example.com");

        // Verify that the method returned the expected UserDetails object
        assertEquals("test@example.com", loadedUser.getUsername());
        assertEquals("password", loadedUser.getPassword());
        assertTrue(loadedUser.getAuthorities().isEmpty());

        // Verify that userDetailsService.loadUserByUsername was called with the correct argument
        verify(userDetailsService).loadUserByUsername("test@example.com");
    }
}