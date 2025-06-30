package com.guidev1911.encurtadorURL.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

import java.time.ZonedDateTime;

public class UrlRequest {
    @NotBlank(message = "A URL original não pode estar vazia.")
    private String OriginalUrl;
    @Schema(example = "2025-06-28T22:09:07.373-03:00")
    @Future(message = "A data de expiração não pode estar no passado.")
    private ZonedDateTime  expirationDate;

    public UrlRequest(String originalUrl, ZonedDateTime expirationDate) {
        OriginalUrl = originalUrl;
        this.expirationDate = expirationDate;
    }

    public String getOriginalUrl() {
        return OriginalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        OriginalUrl = originalUrl;
    }

    public ZonedDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(ZonedDateTime  expirationDate) {
        this.expirationDate = expirationDate;
    }
}
