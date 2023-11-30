package com.bci.challenge.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.bci.challenge.security.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class JwtTokenFilterTest {

    @Test
    public void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        // Mocks
        JwtTokenUtil jwtTokenUtil = Mockito.mock(JwtTokenUtil.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextWrapper securityContextWrapper = Mockito.mock(SecurityContextWrapper.class);
        when(securityContextWrapper.getContext()).thenReturn(securityContext);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        UserDetails userDetails = Mockito.mock(UserDetails.class);

        JwtTokenFilter jwtTokenFilter = new JwtTokenFilter(jwtTokenUtil, securityContextWrapper);

        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtTokenUtil.getUsernameFromToken("validToken")).thenReturn("username");
        when(jwtTokenUtil.loadUserByUsername("username")).thenReturn(userDetails);
        when(jwtTokenUtil.validateToken("validToken", userDetails)).thenReturn(true);

        // Set up the SecurityContextHolder to return mocked SecurityContext
        SecurityContextHolder.setContext(securityContext);

        // Invoke filter
        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        // Assertions for setting authentication
        Mockito.verify(SecurityContextHolder.getContext()).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        Mockito.verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        // Mocks
        JwtTokenUtil jwtTokenUtil = Mockito.mock(JwtTokenUtil.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        SecurityContext securityContextMock = Mockito.mock(SecurityContext.class);

        // Creating a mock for SecurityContextWrapper
        SecurityContextWrapper securityContextWrapperMock = Mockito.mock(SecurityContextWrapper.class);
        JwtTokenFilter jwtTokenFilter = new JwtTokenFilter(jwtTokenUtil, securityContextWrapperMock);

        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
        when(jwtTokenUtil.getUsernameFromToken("invalidToken")).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

        // Stubbing the SecurityContextWrapper behavior
        when(securityContextWrapperMock.getContext()).thenReturn(securityContextMock);

        // Create ListAppender for logging
        Logger logger = (Logger) LoggerFactory.getLogger(JwtTokenFilter.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        // Invoke filter
        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        // Assertions for handling invalid token
        Mockito.verify(securityContextMock, Mockito.never()).setAuthentication(any());
        Mockito.verify(filterChain).doFilter(request, response);

        // Check if the error log contains the expected message
        List<ILoggingEvent> logsList = listAppender.list;
        boolean isErrorMessageLogged = logsList.stream()
                .anyMatch(event -> event.getLevel().equals(Level.ERROR) && event.getMessage().contains("Invalid JWT Token"));
        assertTrue(isErrorMessageLogged);

        // Clean up
        logger.detachAppender(listAppender);
        listAppender.stop();
    }
}