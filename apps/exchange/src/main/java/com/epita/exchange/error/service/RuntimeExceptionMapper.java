package com.epita.exchange.error.service;

import com.epita.exchange.error.controller.response.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Arrays;

@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {
  @Override
  public Response toResponse(RuntimeException exception) {
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(
            new ErrorResponse(
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                exception.getMessage(),
                Arrays.toString(exception.getStackTrace())))
        .build();
  }
}
