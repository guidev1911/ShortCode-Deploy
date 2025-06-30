package com.guidev1911.encurtadorURL.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;


public class UrlResponse {
    private String shortCode;
    private String originalUrl;
    private int clickCount;
    private ZonedDateTime createdAt;
    private ZonedDateTime expirationDate;

    public UrlResponse() {
    }

    public UrlResponse(String shortCode, String originalUrl, int clickCount, ZonedDateTime createdAt, ZonedDateTime expirationDate) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.clickCount = clickCount;
        this.createdAt = createdAt;
        this.expirationDate = expirationDate;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(ZonedDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }
}

