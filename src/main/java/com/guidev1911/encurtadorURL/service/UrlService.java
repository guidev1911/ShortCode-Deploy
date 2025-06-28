package com.guidev1911.encurtadorURL.service;

import com.guidev1911.encurtadorURL.dto.UrlResponse;
import com.guidev1911.encurtadorURL.model.Url;
import com.guidev1911.encurtadorURL.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZoneId;
import java.time.ZonedDateTime;


@Service
public class UrlService {

    @Autowired
    private UrlRepository repository;

    @Autowired
    private UrlServiceValidation validation;

    public Url createShortUrl(String originalUrl, ZonedDateTime expirationDate) {
        if (originalUrl == null || originalUrl.isBlank()) {
            throw new IllegalArgumentException("A URL original não pode estar vazia.");
        }

        if (!validation.isValidUrl(originalUrl)) {
            throw new IllegalArgumentException("A URL fornecida é inválida ou potencialmente maliciosa.");
        }

        ZoneId zoneId = ZoneId.of("America/Sao_Paulo");
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        if (expirationDate == null) {
            expirationDate = now.plusDays(1);
        } else if (expirationDate.isBefore(now)) {
            throw new IllegalArgumentException("A data de expiração não pode estar no passado.");
        } else if (expirationDate.isAfter(now.plusDays(7))) {
            throw new IllegalArgumentException("A data de expiração não pode exceder 7 dias.");
        }

        String shortCode = validation.generateUniqueShortCode();

        Url url = new Url(null, originalUrl, shortCode, expirationDate, now);
        return repository.save(url);
    }
    @Transactional
    public String getOriginalUrl(String shortCode) {
        ZoneId zoneId = ZoneId.of("America/Sao_Paulo");
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        Url url = repository.findByShortCode(shortCode)
                .filter(u -> u.getExpirationDate().isAfter(now))
                .orElseThrow(() -> new IllegalArgumentException("URL não encontrada ou expirada."));

        url.setClickCount(url.getClickCount() + 1);
        repository.save(url);

        return url.getOriginalUrl();
    }

    public UrlResponse getUrlStats(String shortCode) {
        Url url = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new IllegalArgumentException("URL não encontrada."));

        return new UrlResponse(
                url.getShortCode(),
                url.getOriginalUrl(),
                url.getClickCount(),
                url.getCreatedAt(),
                url.getExpirationDate()
        );
    }
}
