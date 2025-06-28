package com.guidev1911.encurtadorURL.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;

public class UrlRequest {
    private String OriginalUrl;
    @Schema(example = "2025-06-28T22:09:07.373-03:00")
    private ZonedDateTime  expirationDate;

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
