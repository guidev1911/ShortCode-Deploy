package com.guidev1911.encurtadorURL.dto;

import java.time.ZonedDateTime;
import java.time.ZoneId;

public class ApiErrorResponse {

    private ZonedDateTime timestamp;
    private int status;
    private String message;

    public ApiErrorResponse(int status, String message, ZonedDateTime now) {
        this.timestamp = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
        this.status = status;
        this.message = message;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
