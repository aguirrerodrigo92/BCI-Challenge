package com.bci.challenge.controller;

import com.bci.challenge.dto.AuthRequest;
import com.bci.challenge.dto.AuthResponse;
import com.bci.challenge.dto.SignUpResponse;
import com.bci.challenge.dto.UserRequest;
import com.bci.challenge.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @PostMapping("/signup")
    public ResponseEntity<Object> signUp(@RequestBody UserRequest userRequest) {
        LOGGER.debug("Received sign-up request. Request details: {}", userRequest);
        SignUpResponse signUpResponse = userService.signUp(userRequest);
        return new ResponseEntity<>(signUpResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        AuthResponse response = userService.authenticateAndGenerateToken(authRequest);
        return ResponseEntity.ok(response);
    }
}