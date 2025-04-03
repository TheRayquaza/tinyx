package com.epita.srvc_home_timeline;

import com.epita.exchange.error.ErrorCode;
import com.epita.srvc_home_timeline.controller.contract.HomeTimelineResponse;
import lombok.Getter;

@Getter
public enum HomeTimelineErrorCode implements ErrorCode {
    USER_NOT_FOUND(404, "User with ID %s not found"),
    UNAUTHORIZED(401, "Unauthorized access"),
    INTERNAL_SERVER_ERROR(500, "Internal server error");

    private final int httpCode;
    private final String messageTemplate;

    HomeTimelineErrorCode(int httpCode, String messageTemplate) {
        this.httpCode = httpCode;
        this.messageTemplate = messageTemplate;
    }

    @Override
    public String getMessage(Object... parameters) {
        if (parameters.length == 0 || parameters == null) {
            return messageTemplate;
        }
        return String.format(messageTemplate, parameters);
    }
}
