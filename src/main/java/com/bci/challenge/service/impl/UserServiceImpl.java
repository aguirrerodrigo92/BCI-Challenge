package com.bci.challenge.service.impl;

import com.bci.challenge.dto.AuthRequest;
import com.bci.challenge.dto.AuthResponse;
import com.bci.challenge.dto.SignUpResponse;
import com.bci.challenge.dto.UserRequest;
import com.bci.challenge.exception.UnauthorizedException;
import com.bci.challenge.exception.UserExistsException;
import com.bci.challenge.exception.UserNotFoundException;
import com.bci.challenge.mapper.PhoneMapper;
import com.bci.challenge.model.Phone;
import com.bci.challenge.model.User;
import com.bci.challenge.repository.UserRepository;
import com.bci.challenge.security.JwtTokenUtil;
import com.bci.challenge.security.PasswordEncryptionUtil;
import com.bci.challenge.security.UserDetailsServiceImpl;
import com.bci.challenge.service.AuthenticationService;
import com.bci.challenge.service.UserService;
import com.bci.challenge.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final PhoneMapper phoneMapper = PhoneMapper.INSTANCE;
    private final UserRepository userRepository;
    private final PasswordEncryptionUtil passwordEncryptionUtil;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationService authenticationService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncryptionUtil passwordEncryptionUtil,
                           JwtTokenUtil jwtTokenUtil,
                           UserDetailsServiceImpl userDetailsService,
                           AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.passwordEncryptionUtil = passwordEncryptionUtil;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.authenticationService = authenticationService;
    }

    @Override
    public SignUpResponse signUp(UserRequest userRequest) {
        // Validation logic for email and password format
        try {
            if (!ValidationUtils.isValidEmail(userRequest.getEmail())) {
                LOGGER.error("Failed to sign up user '{}' due to: {}", userRequest.getEmail(), "Invalid email format");
                throw new IllegalArgumentException("Invalid email format");
            }
            if (!ValidationUtils.isValidPassword(userRequest.getPassword())) {
                LOGGER.error("Failed to sign up user '{}' due to: {}", userRequest.getEmail(), "Invalid password format");
                throw new IllegalArgumentException("Invalid password format");
            }

            // Check if user already exists by email
            if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
                LOGGER.error("Failed to sign up user '{}' due to: {}", userRequest.getEmail(), "User with this email already exists");
                throw new UserExistsException("User with this email already exists");
            }

            var created = LocalDateTime.now();
            var encryptedPassword = passwordEncryptionUtil.encryptPassword(userRequest.getPassword());
            var token = jwtTokenUtil.generateToken(userRequest.getEmail());

            List<Phone> phones = new ArrayList<>();
            if (userRequest.getPhones() != null && !userRequest.getPhones().isEmpty()) {
                phones = userRequest.getPhones().stream().map(phoneMapper::toEntity).collect(Collectors.toList());
            }

            var user = User.builder()
                    .name(userRequest.getName())
                    .password(encryptedPassword)
                    .email(userRequest.getEmail())
                    .created(created)
                    .phones(phones)
                    .build();

            // Save user to the database
            var createdUser = userRepository.save(user);
            LOGGER.info("User '{}' successfully created", userRequest.getEmail());

            return SignUpResponse.builder()
                    .id(createdUser.getId())
                    .created(createdUser.getCreated())
                    .token(token)
                    .build();
        } catch (IllegalArgumentException | UserExistsException ex) {
            LOGGER.error("Failed to sign up user '{}' due to: {}", userRequest.getEmail(), ex.getMessage());
            throw ex; // Re-throwing the caught exception for handling it into global handler
        } catch (Exception ex) {
            LOGGER.error("Unexpected error during sign-up for user '{}'", userRequest.getEmail(), ex);
            throw new RuntimeException("Failed to sign up user");
        }
    }


    @Override
    public AuthResponse authenticateAndGenerateToken(AuthRequest authRequest) {
        try {
            // Check if user exists by email
            if (userRepository.findByEmail(authRequest.getEmail()).isEmpty()) {
                LOGGER.error("Failed to sign up user '{}' due to: {}", authRequest.getEmail(), "User with this email doesn't exist");
                throw new UserNotFoundException("User with this email doesn't exist");
            }
            authenticationService.authenticate(authRequest.getEmail(), authRequest.getPassword());

            final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
            final String token = jwtTokenUtil.generateToken(userDetails.getUsername());

            // Get user information
            var userOptional = userRepository.findByEmail(authRequest.getEmail());
            var user = userOptional.orElseThrow(() -> new UserNotFoundException("User not found"));
            // Updating last login time
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm:ss a");
            String createdFormatted = user.getCreated().format(formatter);
            String lastLoginFormatted = user.getLastLogin().format(formatter);

            return AuthResponse.builder()
                    .id(user.getId())
                    .created(createdFormatted)
                    .lastLogin(lastLoginFormatted)
                    .token(token)
                    .isActive(true) // We're currently not getting any logic for considering a user active, it could be done with authenticationManager flow
                    .name(user.getName())
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .phones(user.getPhones() == null ? null : user.getPhones().stream().map(phoneMapper::toDto).collect(Collectors.toList()))
                    .build();
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Invalid credentials");
        }
    }
}