package com.bci.challenge.service.impl

import com.bci.challenge.dto.AuthRequest
import com.bci.challenge.dto.AuthResponse
import com.bci.challenge.dto.PhoneDto
import com.bci.challenge.dto.SignUpResponse
import com.bci.challenge.dto.UserRequest
import com.bci.challenge.exception.TestAuthenticationException
import com.bci.challenge.exception.UnauthorizedException
import com.bci.challenge.exception.UserExistsException
import com.bci.challenge.exception.UserNotFoundException
import com.bci.challenge.model.User
import com.bci.challenge.repository.UserRepository
import com.bci.challenge.security.JwtTokenUtil
import com.bci.challenge.security.PasswordEncryptionUtil
import com.bci.challenge.security.UserDetailsServiceImpl
import com.bci.challenge.service.AuthenticationService
import org.springframework.security.core.userdetails.UserDetails
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class UserServiceImplSpec extends Specification {

    @Subject
    UserServiceImpl userService

    UserRepository userRepository = Mock()
    PasswordEncryptionUtil passwordEncryptionUtil = Mock()
    JwtTokenUtil jwtTokenUtil = Mock()
    UserDetailsServiceImpl userDetailsService = Mock()
    AuthenticationService authenticationService = Mock()

    def setup() {
        userService = new UserServiceImpl(
                userRepository,
                passwordEncryptionUtil,
                jwtTokenUtil,
                userDetailsService,
                authenticationService
        )
    }

    def "test sign up - success"() {
        given:
        def userRequest = mockUserRequest()

        userRepository.findByEmail(_) >> Optional.empty()
        passwordEncryptionUtil.encryptPassword(_) >> "encryptedPassword"
        jwtTokenUtil.generateToken(_) >> "token"
        userRepository.save(_) >> mockUser()

        when:
        SignUpResponse response = userService.signUp(userRequest)

        then:
        response != null
    }

    def "test signUp - invalid email format"() {
        given:
        def userRequest = new UserRequest(email: "invalid_email_format")

        userRepository.findByEmail(_) >> Optional.empty()

        when:
        userService.signUp(userRequest)

        then:
        thrown(IllegalArgumentException)
    }

    def "test signUp - user exists"() {
        given:
        def userRequest = new UserRequest(name: "John Doe", email: "johndoe@example.com", password: "a2asfGfdfdf4")

        userRepository.findByEmail(_) >> Optional.of(new User())

        when:
        userService.signUp(userRequest)

        then:
        thrown(UserExistsException)
    }

    def "test authenticateAndGenerateToken - success"() {
        given:
        def authRequest = new AuthRequest(email: "johndoe@example.com", password: "a2asfGfdfdf4")
        def user = mockUser()

        userRepository.findByEmail(_) >> Optional.of(user)
        userDetailsService.loadUserByUsername(_) >> mockUserDetails()
        jwtTokenUtil.generateToken(_) >> "token"
        userRepository.save(_) >> user
        authenticationService.authenticate(_, _) >> {}

        when:
        AuthResponse response = userService.authenticateAndGenerateToken(authRequest)

        then:
        response != null
    }

    def "test authenticateAndGenerateToken - user not found"() {
        given:
        def authRequest = new AuthRequest(email: "nonexistent@example.com", password: "somePassword")

        userRepository.findByEmail(_) >> Optional.empty()

        when:
        userService.authenticateAndGenerateToken(authRequest)

        then:
        thrown(UserNotFoundException)
    }

    def "test authenticateAndGenerateToken - invalid credentials"() {
        given:
        def authRequest = new AuthRequest(email: "existing@example.com", password: "wrongPassword")

        userRepository.findByEmail(_) >> Optional.of(mockUser())
        authenticationService.authenticate(_, _) >> {
            throw new TestAuthenticationException("Invalid credentials")
        }

        when:
        userService.authenticateAndGenerateToken(authRequest)

        then:
        thrown(UnauthorizedException)
    }

    private static UserDetails mockUserDetails() {
        return new org.springframework.security.core.userdetails.User(
                "johndoe@example.com",
                "a2asfGfdfdf4",
                Collections.emptyList()
        )
    }

    private static User mockUser() {
        def user = new User(
                id: UUID.fromString("e5c6cf84-8860-4c00-91cd-22d3be28904e"),
                email: "johndoe@example.com",
                password: "a2asfGfdfdf4",
                created: LocalDateTime.parse("2023-11-29T12:51:43")
        )
        return user
    }

    private static UserRequest mockUserRequest() {
        def userRequest = new UserRequest(
                name: "John Doe",
                email: "johndoe@example.com",
                password: "a2asfGfdfdf4",
                phones: [
                        new PhoneDto(number: 12347890, cityCode: 11, countryCode: "+54"),
                        new PhoneDto(number: 98763210, cityCode: 354, countryCode: "+54")
                ]
        )
        return userRequest
    }
}