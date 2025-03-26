package com.epita.exchange.auth.service;

import com.epita.exchange.auth.service.entity.AuthEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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
}
