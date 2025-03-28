package com.epita.exchange.utils;

import org.slf4j.LoggerFactory;

public interface Logger {
  default org.slf4j.Logger logger() {
    return LoggerFactory.getLogger(getClass());
  }
}
