package com.bci.challenge.service;

import com.bci.challenge.dto.AuthRequest;
import com.bci.challenge.dto.AuthResponse;
import com.bci.challenge.dto.SignUpResponse;
import com.bci.challenge.exception.UserExistsException;
import com.bci.challenge.dto.UserRequest;

public interface UserService {
    SignUpResponse signUp(UserRequest userRequest);

    AuthResponse authenticateAndGenerateToken(AuthRequest authRequest);
}
