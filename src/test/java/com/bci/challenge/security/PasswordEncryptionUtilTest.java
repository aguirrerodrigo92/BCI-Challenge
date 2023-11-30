package com.bci.challenge.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PasswordEncryptionUtilTest {

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void testEncryptPassword() {
        String rawPassword = "testPassword";
        String encryptedPassword = "encryptedTestPassword";

        // Mock behavior for password encoder
        when(passwordEncoder.encode(rawPassword)).thenReturn(encryptedPassword);

        // Instantiate the PasswordEncryptionUtil with the mocked password encoder
        PasswordEncryptionUtil passwordEncryptionUtil = new PasswordEncryptionUtil(passwordEncoder);

        // Test password encryption
        String result = passwordEncryptionUtil.encryptPassword(rawPassword);

        // Verify that encryption is performed and the expected encrypted password is returned
        assertNotNull(result);
        assertEquals(encryptedPassword, result);
    }

    @Test
    public void testMatchPassword() {
        String rawPassword = "testPassword";
        String encryptedPassword = "encryptedTestPassword";

        // Mock behavior for password encoder
        when(passwordEncoder.matches(rawPassword, encryptedPassword)).thenReturn(true);

        // Instantiate the PasswordEncryptionUtil with the mocked password encoder
        PasswordEncryptionUtil passwordEncryptionUtil = new PasswordEncryptionUtil(passwordEncoder);

        // Test password matching
        boolean result = passwordEncryptionUtil.matchPassword(rawPassword, encryptedPassword);

        // Verify that the match method returns true
        assertTrue(result);
    }
}