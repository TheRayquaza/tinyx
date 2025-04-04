package com.epita.exchange.error.controller.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
  private final int status;
  private final String message;
  private final String stackTrace;
}
