package com.epita.exchange.error;

import com.epita.exchange.error.controller.response.ErrorResponse;
import jakarta.ws.rs.core.Response;
import java.util.Arrays;
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
      return Response.status(errorCode.getHttpCode())
          .entity(
              new ErrorResponse(
                  errorCode.getHttpCode(), getMessage(), Arrays.toString(getStackTrace())))
          .build();
    }

    @Override
    public String getMessage() {
      return errorCode.getMessage(parameters);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
      return this; // Prevent stack traces
    }
  }

  default RuntimeError createError(Object... parameters) {
    return new RuntimeError(this, parameters);
  }

  default RuntimeError createError() {
    return createError((Object[]) null);
  }
}
