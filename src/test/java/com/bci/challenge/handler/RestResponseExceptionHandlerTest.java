package com.bci.challenge.handler;

import com.bci.challenge.dto.ErrorResponse;
import com.bci.challenge.exception.UnauthorizedException;
import com.bci.challenge.exception.UserExistsException;
import com.bci.challenge.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

public class RestResponseExceptionHandlerTest {

    private final RestResponseExceptionHandler exceptionHandler = new RestResponseExceptionHandler();

    @Test
    public void testHandleUnauthorizedException() {
        UnauthorizedException ex = new UnauthorizedException("Unauthorized access");
        ResponseEntity<Object> responseEntity = exceptionHandler.handleUnauthorizedException(ex);
        assertErrorResponse(responseEntity, HttpStatus.UNAUTHORIZED, "Unauthorized access");
    }

    @Test
    public void testHandleUserExistsException() {
        UserExistsException ex = new UserExistsException("User already exists");
        ResponseEntity<Object> responseEntity = exceptionHandler.handleUserExistsException(ex);
        assertErrorResponse(responseEntity, HttpStatus.CONFLICT, "User already exists");
    }

    @Test
    public void testHandleUserNotFoundException() {
        UserNotFoundException ex = new UserNotFoundException("User not found");
        ResponseEntity<Object> responseEntity = exceptionHandler.handleUserNotFoundException(ex);
        assertErrorResponse(responseEntity, HttpStatus.NOT_FOUND, "User not found");
    }

    @Test
    public void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        ResponseEntity<Object> responseEntity = exceptionHandler.handleIllegalArgumentException(ex);
        assertErrorResponse(responseEntity, HttpStatus.BAD_REQUEST, "Invalid argument");
    }

    @Test
    public void testHandleException() {
        Exception ex = new Exception("Unexpected error");
        ResponseEntity<Object> responseEntity = exceptionHandler.handleException(ex);
        assertErrorResponse(responseEntity, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
    }

    @Test
    public void testHandleThrowable() {
        ResponseEntity<Object> responseEntity = exceptionHandler.handleThrowable();
        assertErrorResponse(responseEntity, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    private void assertErrorResponse(ResponseEntity<Object> responseEntity, HttpStatus expectedStatus, String expectedDetail) {
        assertEquals(expectedStatus, responseEntity.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedDetail, errorResponse.getDetail());
        assertNotNull(errorResponse.getTimestamp()); // Optionally assert timestamp
        // You can add more assertions for other fields in ErrorResponse if needed
    }
}