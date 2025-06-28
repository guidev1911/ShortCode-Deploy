package com.guidev1911.encurtadorURL.dto;

import java.time.LocalDateTime;

import java.time.ZonedDateTime;
import java.time.ZoneId;

public class ApiErrorResponse {

    private ZonedDateTime timestamp;
    private int status;
    private String error;

    public ApiErrorResponse(int status, String error) {
        this.timestamp = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
        this.status = status;
        this.error = error;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}
