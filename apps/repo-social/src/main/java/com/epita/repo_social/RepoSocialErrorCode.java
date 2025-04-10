package com.epita.repo_social;

import com.epita.exchange.error.ErrorCode;
import lombok.Getter;

@Getter
public enum RepoSocialErrorCode implements ErrorCode {
  BAD_REQUEST(400, "Bad request: %s"),
  UNAUTHORIZED(401, "Unauthorized access: %s"),
  FORBIDDEN(403, "Forbidden action: %s"),
  NOT_FOUND(404, "Resource not found: %s"),
  ERROR_DURING_CYPHER_EXEC(500, "Error during the cypher script execution: %s"),
  INTERNAL_SERVER_ERROR(500, "Internal server error: %s");

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
