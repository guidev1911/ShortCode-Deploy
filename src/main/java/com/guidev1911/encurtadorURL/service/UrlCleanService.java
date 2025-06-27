package com.guidev1911.encurtadorURL.service;

import com.guidev1911.encurtadorURL.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UrlCleanService {

    @Autowired
    private UrlRepository repository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void removeExpiredUrls() {
        repository.deleteByExpirationDateBefore(LocalDateTime.now());
    }
}