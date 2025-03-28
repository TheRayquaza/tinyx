package com.epita.exchange.auth.service;

import com.epita.exchange.auth.service.entity.AuthEntity;
import com.epita.exchange.utils.Logger;
import io.quarkus.security.UnauthorizedException;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import java.util.Base64;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter, Logger {

  @Inject AuthContext authContext;

  @Override
  public void filter(ContainerRequestContext requestContext) {
    String bearerToken = requestContext.getHeaderString("Authorization");
    if (requestContext.getUriInfo().getPath().equals("/login")
        || (requestContext.getUriInfo().getPath().equals("/user")
            && requestContext.getRequest().getMethod().equals("POST"))) {
      logger().info("Ignoring login and account creation requests");
    } else if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      String token = bearerToken.substring(7);
      logger().debug("Intercepting bearer token: %s", token);
      authContext.setAuthEntity(decodeAuthEntity(token));
    } else {
      logger().error("Missing or invalid Bearer token: %s", bearerToken);
      throw new UnauthorizedException("Missing or invalid Bearer token");
    }
  }

  private AuthEntity decodeAuthEntity(String token) {
    String decodedString = new String(Base64.getDecoder().decode(token));
    String[] parts = decodedString.split(",", 1);
    String userId = parts[0];
    String username = parts[1];
    return new AuthEntity(userId, username);
  }
}
