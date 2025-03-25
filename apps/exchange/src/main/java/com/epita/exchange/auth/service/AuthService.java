package com.epita.exchange.auth.service;

import com.epita.exchange.auth.service.entity.AuthEntity;
import io.quarkus.security.UnauthorizedException;

import jakarta.enterprise.context.ApplicationScoped;

import com.epita.exchange.utils.Logger;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import java.util.Base64;

@ApplicationScoped
@Provider
public class AuthService implements ContainerRequestFilter, Logger {

    private static final ThreadLocal<AuthEntity> authEntityThreadLocal = new ThreadLocal<>();

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String bearerToken = requestContext.getHeaderString("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            authEntityThreadLocal.set(decodeAuthEntity(token));
        } else {
            this.logger().error("Missing or invalid Bearer token");
            throw new UnauthorizedException("Missing or invalid Bearer token");
        }
    }

    private AuthEntity decodeAuthEntity(String token) {
        String decodedString = new String(Base64.getDecoder().decode(token));
        String[] parts = decodedString.split(",");
        String userId = parts[0];
        String username = parts[1];
        String email = parts[2];

        return new AuthEntity(userId, username, email);
    }

    private AuthEntity getAuthEntity() {
        return authEntityThreadLocal.get();
    }

    public String getUserId() {
        AuthEntity authEntity = getAuthEntity();
        return (authEntity != null) ? authEntity.getUserId() : null;
    }

    public String getUsername() {
        AuthEntity authEntity = getAuthEntity();
        return (authEntity != null) ? authEntity.getUsername() : null;
    }
}
