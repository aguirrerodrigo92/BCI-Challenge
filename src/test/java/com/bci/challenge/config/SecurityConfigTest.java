package com.bci.challenge.config;

import com.bci.challenge.filter.SecurityContextWrapper;
import com.bci.challenge.security.JwtTokenUtil;
import com.bci.challenge.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private SecurityContextWrapper securityContextWrapper;

    @MockBean
    private AuthenticationManagerBuilder authManagerBuilder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void accessLoginWithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/login"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void authenticationManagerBean_ShouldReturnNonNullAuthenticationManager() throws Exception {
        assertNotNull(authenticationManager);
    }

    @Test
    void passwordEncoder_ShouldReturnNonNullBCryptPasswordEncoder() {
        // Test that the passwordEncoder method returns a BCryptPasswordEncoder
        SecurityConfig securityConfig = new SecurityConfig(jwtTokenUtil, userDetailsService, securityContextWrapper);
        BCryptPasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        assertNotNull(passwordEncoder);
    }

    @Test
    void configureAuthenticationManagerBuilder_ShouldCallUserDetailsService() throws Exception {
        // Test that the configure method for AuthenticationManagerBuilder calls userDetailsService
        SecurityConfig securityConfig = new SecurityConfig(jwtTokenUtil, userDetailsService, securityContextWrapper);
        securityConfig.configure(authManagerBuilder);
        verify(authManagerBuilder).userDetailsService(userDetailsService);
    }
}