package com.epita.exchange.auth.service;

import com.epita.exchange.auth.service.entity.AuthEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

@ApplicationScoped
public class AuthService {

  @Inject AuthContext authContext;

  public String getUserId() {
    AuthEntity authEntity = authContext.getAuthEntity();
    return (authEntity != null) ? authEntity.getUserId() : null;
  }

  public String getUsername() {
    AuthEntity authEntity = authContext.getAuthEntity();
    return (authEntity != null) ? authEntity.getUsername() : null;
  }

  public static String generateToken(String id, String username) {
    String token = id + "," + username;
    byte[] new_token = token.getBytes(StandardCharsets.UTF_8);

    return Arrays.toString(Base64.getEncoder().encode(new_token));
  }
}
