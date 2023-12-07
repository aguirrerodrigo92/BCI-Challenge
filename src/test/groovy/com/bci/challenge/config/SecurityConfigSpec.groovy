package com.bci.challenge.config

import com.bci.challenge.filter.SecurityContextWrapper
import com.bci.challenge.security.JwtTokenUtil
import com.bci.challenge.security.UserDetailsServiceImpl
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigSpec extends Specification {

    @Shared
    @AutoCleanup
    JwtTokenUtil jwtTokenUtil = Mock()

    @Shared
    @AutoCleanup
    UserDetailsServiceImpl userDetailsService = Mock()

    @Shared
    @AutoCleanup
    SecurityContextWrapper securityContextWrapper = Mock()

    @Shared
    @AutoCleanup
    AuthenticationManager authenticationManager = Mock()

    def "authenticationManagerBean_ShouldReturnNonNullAuthenticationManager"() {
        expect:
        authenticationManager != null
    }

    def "passwordEncoder_ShouldReturnNonNullBCryptPasswordEncoder"() {
        given:
        SecurityConfig securityConfig = new SecurityConfig(jwtTokenUtil, userDetailsService, securityContextWrapper)

        when:
        BCryptPasswordEncoder passwordEncoder = securityConfig.passwordEncoder()

        then:
        passwordEncoder != null
    }

}