package com.epita.srvc_media;

import com.epita.exchange.error.ErrorCode;
import lombok.Getter;

@Getter
public enum SrvcMediaErrorCode implements ErrorCode {
  INVALID_MEDIA_ID(400, "Invalid user data: %s"),
  MEDIA_DOWNLOAD_FAILED(404, "Media with ID '%s' not found"),
  INTERNAL_SERVER_ERROR(500, "Internal server error");

  private final int httpCode;
  private final String messageTemplate;

  SrvcMediaErrorCode(int httpCode, String messageTemplate) {
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
