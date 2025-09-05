package com.github.ideantifyserver.global.security.authentication;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@Configuration
public class UserAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (authentication instanceof UserAuthentication) return authentication;

        throw new AuthenticationException("Unsupported authentication type") {
        };
    }

    @Override
    public boolean supports(Class<?> authentication) {

        return UserAuthentication.class.isAssignableFrom(authentication);
    }
}
