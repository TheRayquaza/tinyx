package com.epita.exchange.utils;

import javax.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;

public interface ErrorCode {

  int getHttpCode();

  String getMessage(Object... parameters);

  @Getter
  @AllArgsConstructor
  class RuntimeError extends RuntimeException {
    private final ErrorCode errorCode;
    private final Object[] parameters;

    public Response asResponse() {
      return Response.status(errorCode.getHttpCode()).entity(getMessage()).build();
    }

    @Override
    public String getMessage() {
      return errorCode.getMessage(parameters);
    }
  }

  default RuntimeError createError(Object... parameters) {
    return new RuntimeError(this, parameters);
  }

  default RuntimeError createError() {
    return createError((Object[]) null);
  }
}
