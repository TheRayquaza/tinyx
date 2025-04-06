package com.epita.srvc_search;

import com.epita.exchange.error.ErrorCode;
import lombok.Getter;

@Getter
public enum SrvcSearchErrorCode implements ErrorCode {
  USER_ALREADY_EXISTS(400, "User with userId: %s already exists"),
  ALREADY_BLOCKED(400, "%s is already blocked for userId: %s"),
  NOT_BLOCKED(400, "%s is not blocked for userId: %s"),
  INVALID_QUERY(400, "Invalid query: %s"),
  INTERNAL_SERVER_ERROR(500, "Internal server error");

  private final int httpCode;
  private final String messageTemplate;

  SrvcSearchErrorCode(int httpCode, String messageTemplate) {
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
