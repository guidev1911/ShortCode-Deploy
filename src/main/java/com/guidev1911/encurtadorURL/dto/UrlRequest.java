package com.guidev1911.encurtadorURL.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class UrlRequest {
    private String OriginalUrl;
    private LocalDateTime expirationDate;

    public String getOriginalUrl() {
        return OriginalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        OriginalUrl = originalUrl;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }
}
