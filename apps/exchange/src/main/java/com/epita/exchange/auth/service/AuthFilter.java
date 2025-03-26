package com.epita.exchange.auth.service;

import com.epita.exchange.auth.service.entity.AuthEntity;
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
public class AuthFilter implements ContainerRequestFilter {

  @Inject AuthContext authContext;

  @Override
  public void filter(ContainerRequestContext requestContext) {
    String bearerToken = requestContext.getHeaderString("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      String token = bearerToken.substring(7);
      authContext.setAuthEntity(decodeAuthEntity(token));
    } else {
      throw new UnauthorizedException("Missing or invalid Bearer token");
    }
  }

  private AuthEntity decodeAuthEntity(String token) {
    String decodedString = new String(Base64.getDecoder().decode(token));
    String[] parts = decodedString.split(",");
    String userId = parts[0];
    String username = parts[1];
    return new AuthEntity(userId, username);
  }
}
