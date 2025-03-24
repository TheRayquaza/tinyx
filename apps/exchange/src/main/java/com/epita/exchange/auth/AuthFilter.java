package com.epita.exchange.auth;

import com.epita.exchange.auth.repository.entity.UserLoginCommandModel;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;

import com.epita.exchange.auth.repository.UserLoginCommandRepository;
import io.quarkus.security.identity.SecurityIdentity;
import com.epita.exchange.utils.Logger;

@Authenticated
@Provider
public class AuthFilter implements ContainerRequestFilter, Logger {

    @Inject
    UserLoginCommandRepository userLoginCommandRepository;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String bearerToken = requestContext.getHeaderString("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            UserLoginCommandModel loginCommand = userLoginCommandRepository.find("bearerToken", token).firstResult();

            if (loginCommand == null || !loginCommand.isAuthenticated()) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Authentication failed")
                        .build());
            }
        } else {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Missing Bearer Token")
                    .build());
        }
    }
}
