package com.epita.exchange.auth.service;

import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import javax.security.auth.login.LoginException;
import java.util.UUID;
import java.util.Optional;

import com.epita.exchange.utils.Logger;

@ApplicationScoped
public class AuthService implements Logger {

    @Inject
    SecurityIdentity securityIdentity;

    @ConfigProperty(name = "jwt.user.claim", defaultValue = "userId")
    String userClaim;

    public void onStart(@Observes StartupEvent ev) {
        this.logger().info("AuthService started successfully.");
    }

    public String getUserId() throws LoginException {
        if (securityIdentity == null) {
            this.logger().error("No security identity available.");
            throw new LoginException("User is not authenticated.");
        }

        Optional<String> userIdClaim = securityIdentity.getAttributes().containsKey(userClaim)
                ? Optional.ofNullable(securityIdentity.getAttribute(userClaim).toString())
                : Optional.empty();

        if (userIdClaim.isPresent()) {
            try {
                return UUID.fromString(userIdClaim.get()).toString();
            } catch (IllegalArgumentException e) {
                this.logger().error("Invalid UUID format in user claim.", e);
                throw new LoginException("Invalid user ID format in the token.");
            }
        } else {
            this.logger().error("User ID claim not found in the JWT token.");
            throw new LoginException("User ID claim not found in the token.");
        }
    }
}
