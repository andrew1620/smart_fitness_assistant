package com.andrew.smartfitnessassistant.service;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;


public class TelegramAuthenticationToken extends AbstractAuthenticationToken {
    @Getter
    private final String chatId;
    private final Object principal;
    private final Collection<GrantedAuthority> authorities;

    public TelegramAuthenticationToken(String chatId, Object principal,
                                       Collection<GrantedAuthority> authorities) {
        super(authorities);
        this.chatId = chatId;
        this.principal = principal;
        this.authorities = new ArrayList<>(authorities);
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public boolean isAdmin() {
        return authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}