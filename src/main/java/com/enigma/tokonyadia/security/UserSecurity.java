package com.enigma.tokonyadia.security;

import com.enigma.tokonyadia.entity.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserSecurity {
    public void validateUserById(String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        if (!principal.getUserId().equals(userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "anda tidak di izinkan untuk mengakses resource ini");
    }

    public void validateUserByEmail(String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        if (!principal.getEmail().equals(email))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "anda tidak di izinkan untuk mengakses resource ini");
    }
}
