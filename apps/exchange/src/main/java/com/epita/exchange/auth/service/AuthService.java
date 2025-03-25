package com.epita.exchange.auth.service;

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

    private static ThreadLocal<AuthModel> authModelThreadLocal = new ThreadLocal<>();

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String bearerToken = requestContext.getHeaderString("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            String decodedString = new String(Base64.getDecoder().decode(token));
            AuthModel authModel = decodeAuthModel(decodedString);
            authModelThreadLocal.set(authModel);
        } else {
            this.logger().error("Missing or invalid Bearer token");
            throw new UnauthorizedException("Missing or invalid Bearer token");
        }
    }

    private AuthModel decodeAuthModel(String decodedString) {
        String[] parts = decodedString.split(",");
        String userId = parts[0];
        String username = parts[1];
        String email = parts[2];

        return new AuthModel(userId, username, email);
    }

    private AuthModel getAuthModel() {
        return authModelThreadLocal.get();
    }

    public String getUserId() {
        AuthModel authModel = getAuthModel();
        return (authModel != null) ? authModel.getUserId() : null;
    }

    public String getUsername() {
        AuthModel authModel = getAuthModel();
        return (authModel != null) ? authModel.getUsername() : null;
    }
}
