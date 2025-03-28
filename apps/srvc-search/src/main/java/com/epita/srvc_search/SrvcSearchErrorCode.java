package com.epita.srvc_search;

import com.epita.exchange.error.ErrorCode;

import lombok.Getter;

@Getter
public enum SrvcSearchErrorCode implements ErrorCode {
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
