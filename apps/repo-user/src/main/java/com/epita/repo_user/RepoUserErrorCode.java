package com.epita.repo_user;

import com.epita.exchange.error.ErrorCode;
import lombok.Getter;

@Getter
public enum RepoUserErrorCode implements ErrorCode {
  USER_NOT_FOUND(404, "User with ID '%s' not found"),
  USER_WITH_USERNAME_FOUND(404, "User with username '%s' not found"),
  USER_WITH_USERNAME_ALREADY_EXISTS(409, "User with username '%s', already exists"),
  USER_ALREADY_EXISTS(409, "User with field '%s' already exists"),
  INVALID_USER_DATA(400, "Invalid user data: %s"),
  UNAUTHORIZED(401, "Unauthorized access"),
  FORBIDDEN(403, "Forbidden action"),
  INTERNAL_SERVER_ERROR(500, "Internal server error");

  private final int httpCode;
  private final String messageTemplate;

  RepoUserErrorCode(int httpCode, String messageTemplate) {
    this.httpCode = httpCode;
    this.messageTemplate = messageTemplate;
  }

  @Override
  public String getMessage(Object... parameters) {
    if (parameters == null || parameters.length == 0) {
      return messageTemplate;
    }
    return String.format(messageTemplate, parameters);
  }
}
