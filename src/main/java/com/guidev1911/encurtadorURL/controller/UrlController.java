package com.guidev1911.encurtadorURL.controller;

import com.guidev1911.encurtadorURL.controller.swagger.UrlControllerDocs;
import com.guidev1911.encurtadorURL.dto.UrlRequest;
import com.guidev1911.encurtadorURL.dto.UrlResponse;
import com.guidev1911.encurtadorURL.model.Url;
import com.guidev1911.encurtadorURL.service.UrlService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "URL", description = "Endpoints para criar URLs e consultar dados da URL ")
public class UrlController implements UrlControllerDocs {

    @Autowired
    private UrlService service;


    @PostMapping("/shorten")
    @Override
    public ResponseEntity<Object> shorten(@RequestBody UrlRequest request) {
        Url url = service.createShortUrl(request.getOriginalUrl(), request.getExpirationDate());

        Map<String, String> response = new HashMap<>();
        response.put("shortCode", "localhost:8080/"+url.getShortCode());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}")
    @Override
    public ResponseEntity<?> redirect(@PathVariable String shortCode) {
        try {
            String originalUrl = service.getOriginalUrl(shortCode);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(originalUrl))
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/stats/{shortCode}")
    @Override
    public ResponseEntity<?> getStats(@PathVariable String shortCode) {
        try {
            UrlResponse response = service.getUrlStats(shortCode);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}