package com.bci.challenge.service.impl


import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import spock.lang.Specification

class AuthenticationServiceImplSpec extends Specification {

    AuthenticationManager authenticationManager = Mock()

    def "test authenticate"() {
        given:
        AuthenticationServiceImpl authenticationService = new AuthenticationServiceImpl(authenticationManager)

        // Test data
        def username = "userTest"
        def password = "passwordTest"

        when:
        authenticationService.authenticate(username, password)

        then:
        1 * authenticationManager.authenticate(_ as UsernamePasswordAuthenticationToken) >> {}
    }
}