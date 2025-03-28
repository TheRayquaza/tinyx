package com.epita.exchange.auth.service;

import com.epita.exchange.auth.service.entity.AuthEntity;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AuthContext {
  private AuthEntity authEntity;

  public AuthEntity getAuthEntity() {
    return authEntity;
  }

  public void setAuthEntity(AuthEntity authEntity) {
    this.authEntity = authEntity;
  }
}
