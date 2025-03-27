package com.epita.exchange.error.service;

import com.epita.exchange.error.ErrorCode;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalErrorMapper implements ExceptionMapper<ErrorCode.RuntimeError> {
  @Override
  public Response toResponse(ErrorCode.RuntimeError exception) {
    return exception.asResponse();
  }
}
