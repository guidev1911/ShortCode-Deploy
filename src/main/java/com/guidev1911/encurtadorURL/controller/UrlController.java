package com.guidev1911.encurtadorURL.controller;

import com.guidev1911.encurtadorURL.controller.swagger.UrlControllerDocs;
import com.guidev1911.encurtadorURL.dto.UrlRequest;
import com.guidev1911.encurtadorURL.dto.UrlResponse;
import com.guidev1911.encurtadorURL.model.Url;
import com.guidev1911.encurtadorURL.service.UrlService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@Validated
@Tag(name = "URL", description = "Endpoints para criar URLs e consultar dados da URL ")
public class UrlController implements UrlControllerDocs {

    @Autowired
    private UrlService service;


    @PostMapping("/shorten")
    @Override
    public ResponseEntity<Object> shorten(@RequestBody @Valid UrlRequest request) {
        Url url = service.createShortUrl(request.getOriginalUrl(), request.getExpirationDate());

        Map<String, String> response = new HashMap<>();
        response.put("shortCode", "scd-gowv.onrender.com/"+url.getShortCode());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}")
    @Override
    public ResponseEntity<?> redirect(@PathVariable @NotBlank(message = "URL não pode estar vazia")  String shortCode) {
            String originalUrl = service.getOriginalUrl(shortCode);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(originalUrl))
                    .build();
    }

    @GetMapping("/stats/{shortCode}")
    @Override
    public ResponseEntity<?> getStats(@PathVariable @NotBlank(message = "URL encurtada está vazia") String shortCode) {
            UrlResponse response = service.getUrlStats(shortCode);
            return ResponseEntity.ok(response);
    }

}