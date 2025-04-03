package com.epita.repo_social;

import com.epita.exchange.error.ErrorCode;
import lombok.Getter;

@Getter
public enum RepoSocialErrorCode implements ErrorCode {
  ERROR_DURING_CYPHER_EXEC(404, "Error during the cypher script execution"),
  INVALID_USER_DATA(400, "Invalid user data: %s"),
  UNAUTHORIZED(401, "Unauthorized access"),
  FORBIDDEN(403, "Forbidden action"),
  INTERNAL_SERVER_ERROR(500, "Internal server error");

  private final int httpCode;
  private final String messageTemplate;

  RepoSocialErrorCode(int httpCode, String messageTemplate) {
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
