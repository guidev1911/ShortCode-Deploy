package com.guidev1911.encurtadorURL.dto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class UrlRequest {
    private String OriginalUrl;
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
