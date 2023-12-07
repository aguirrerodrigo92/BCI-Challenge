package com.bci.challenge.controller;

import com.bci.challenge.dto.*;
import com.bci.challenge.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create a new user")
    @PostMapping("/signup")
    @ApiResponse(responseCode = "201", description = "User successfully created",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = SignUpResponse.class),
                    examples = {@ExampleObject(name = "Successful SignUp",
                            value = "{\"id\": \"2f5b980b-3c6a-4ed1-92cb-5f29ef674c89\", \"created\": \"2023-12-01T12:00:00\", \"token\": \"jwt-token\"}")})})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Invalid email format or password format",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(name = "Invalid Format",
                                    value = "{\"timestamp\": \"2023-12-01T12:00:00\", \"code\": 400, \"detail\": \"Invalid email format or password format\"}")})}),
            @ApiResponse(responseCode = "409", description = "User with this email already exists",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(name = "User Exists",
                                    value = "{\"timestamp\": \"2023-12-01T12:00:00\", \"code\": 409, \"detail\": \"User with this email already exists\"}")})}),
            @ApiResponse(responseCode = "500", description = "Unexpected error occurred",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(name = "Unexpected Error",
                                    value = "{\"timestamp\": \"2023-12-01T12:00:00\", \"code\": 500, \"detail\": \"Unexpected error occurred\"}")})})
    })
    public ResponseEntity<SignUpResponse> signUp(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User details", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserRequest.class),
                            examples = {@ExampleObject(name = "SignUp Request",
                                    value = "{\"name\": \"John Doe\", \"email\": \"johndoe@example.com\", \"password\": \"a2asfGfdfdf4\", \"phones\": [{\"number\": 87650009, \"cityCode\": 11, \"countryCode\": \"+54\"}]}")})
            })
            @RequestBody UserRequest userRequest) {
        LOGGER.debug("Received sign-up request. Request details: {}", userRequest);
        SignUpResponse signUpResponse = userService.signUp(userRequest);
        return new ResponseEntity<>(signUpResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "Authenticate user and generate token")
    @PostMapping("/login")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Login successful",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = AuthResponse.class),
                    examples = {@ExampleObject(name = "Successful Login",
                            value = "{\"id\": \"67890\", \"created\": \"2023-12-01T12:00:00\", \"lastLogin\": \"2023-12-01T12:05:00\", \"token\": \"jwt-token\", \"isActive\": true, \"name\": \"John Doe\", \"email\": \"john@example.com\", \"password\": \"abc123\", \"phones\": [{\"number\": 87650009, \"cityCode\": 11, \"countryCode\": \"+54\"}]}")})})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Invalid credentials",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(name = "Invalid Credentials",
                                    value = "{\"timestamp\": \"2023-12-01T12:00:00\", \"code\": 400, \"detail\": \"Invalid credentials\"}")})}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User with this email doesn't exist",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(name = "User Not Found",
                                    value = "{\"timestamp\": \"2023-12-01T12:00:00\", \"code\": 404, \"detail\": \"User with this email doesn't exist\"}")})}),
            @ApiResponse(responseCode = "500", description = "Unexpected error occurred",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(name = "Unexpected Error",
                                    value = "{\"timestamp\": \"2023-12-01T12:00:00\", \"code\": 500, \"detail\": \"Unexpected error occurred\"}")})})
    })
    public ResponseEntity<AuthResponse> login(
            @ParameterObject
            @RequestBody AuthRequest authRequest) {
        AuthResponse response = userService.authenticateAndGenerateToken(authRequest);
        return ResponseEntity.ok(response);
    }
}