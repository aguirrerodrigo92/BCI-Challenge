package com.bci.challenge.filter;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextWrapper {
    public SecurityContext getContext() {
        return SecurityContextHolder.getContext();
    }
}