package com.guidev1911.encurtadorURL.model;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "url")
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String originalUrl;

    @Column(nullable = false, unique = true, length = 10)
    private String shortCode;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Column(nullable = false)
    private int clickCount;

    public Url() {
    }

    public Url(Long id, String originalUrl, String shortCode, LocalDateTime expirationDate, LocalDateTime createdAt) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.expirationDate = expirationDate;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Url url = (Url) o;
        return getClickCount() == url.getClickCount() && Objects.equals(getId(), url.getId()) && Objects.equals(getOriginalUrl(), url.getOriginalUrl()) && Objects.equals(getShortCode(), url.getShortCode()) && Objects.equals(getCreatedAt(), url.getCreatedAt()) && Objects.equals(getExpirationDate(), url.getExpirationDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getOriginalUrl(), getShortCode(), getCreatedAt(), getExpirationDate(), getClickCount());
    }
}
