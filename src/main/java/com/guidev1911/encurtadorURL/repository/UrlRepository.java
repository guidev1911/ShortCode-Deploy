package com.guidev1911.encurtadorURL.repository;

import com.guidev1911.encurtadorURL.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortCode(String shortCode);
    int deleteByExpirationDateBefore(LocalDateTime dateTime);
    boolean existsByShortCode(String shortCode);

}

