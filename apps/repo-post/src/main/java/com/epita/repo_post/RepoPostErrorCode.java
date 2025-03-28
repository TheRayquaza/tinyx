package com.epita.repo_post;

import com.epita.exchange.error.ErrorCode;
import lombok.Getter;

@Getter
public enum RepoPostErrorCode implements ErrorCode {
  POST_NOT_FOUND(404, "Post with ID %s not found"),
  OWNER_NOT_FOUND(404, "Owner with ID %s not found"),
  INVALID_POST_DATA(400, "Invalid post data: %s"),
  UNAUTHORIZED(401, "Unauthorized access"),
  FORBIDDEN(401, "Forbidden action"),
  INTERNAL_SERVER_ERROR(500, "Internal server error");

  private final int httpCode;
  private final String messageTemplate;

  RepoPostErrorCode(int httpCode, String messageTemplate) {
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
