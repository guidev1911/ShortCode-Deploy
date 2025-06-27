package com.guidev1911.encurtadorURL.unitTests.service;

import static org.junit.jupiter.api.Assertions.*;

import com.guidev1911.encurtadorURL.dto.UrlResponse;
import com.guidev1911.encurtadorURL.model.Url;
import com.guidev1911.encurtadorURL.repository.UrlRepository;
import com.guidev1911.encurtadorURL.service.UrlService;
import com.guidev1911.encurtadorURL.service.UrlServiceValidation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository repository;

    @Mock
    private UrlServiceValidation validation;

    @InjectMocks
    private UrlService service;

    private final String ORIGINAL_URL = "https://www.youtube.com/";


    @Test
    void deveCriarShortUrlComDataPadrao() {
        when(validation.isValidUrl(ORIGINAL_URL)).thenReturn(true);

        Url mockUrl = new Url(1L, ORIGINAL_URL, "abc123", LocalDateTime.now().plusDays(1), LocalDateTime.now());
        when(repository.save(any())).thenReturn(mockUrl);

        Url result = service.createShortUrl(ORIGINAL_URL, null);

        assertNotNull(result);
        assertEquals(ORIGINAL_URL, result.getOriginalUrl());
        assertEquals("abc123", result.getShortCode());
        verify(repository).save(any(Url.class));
    }

    @Test
    void deveLancarExcecaoQuandoUrlForVazia() {
        Exception e = assertThrows(IllegalArgumentException.class, () ->
                service.createShortUrl(" ", null)
        );
        assertEquals("A URL original não pode estar vazia.", e.getMessage());
    }

    @Test
    void deveLancarExcecaoSeDataExpirada() {
        when(validation.isValidUrl(ORIGINAL_URL)).thenReturn(true);

        LocalDateTime passada = LocalDateTime.now().minusDays(2);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                service.createShortUrl(ORIGINAL_URL, passada)
        );

        assertEquals("A data de expiração não pode estar no passado.", e.getMessage());
    }

    @Test
    void deveLancarExcecaoSeDataMaiorQue7Dias() {
        when(validation.isValidUrl(ORIGINAL_URL)).thenReturn(true);

        LocalDateTime longa = LocalDateTime.now().plusDays(8);
        Exception e = assertThrows(IllegalArgumentException.class, () ->
                service.createShortUrl(ORIGINAL_URL, longa)
        );

        assertEquals("A data de expiração não pode exceder 7 dias.", e.getMessage());
    }

    @Test
    void deveRetornarUrlOriginalEIncrementarClick() {
        Url url = new Url(1L, ORIGINAL_URL, "abc123", LocalDateTime.now().plusDays(1), LocalDateTime.now());
        url.setClickCount(0);

        when(repository.findByShortCode("abc123")).thenReturn(Optional.of(url));
        when(repository.save(any())).thenReturn(url);

        String result = service.getOriginalUrl("abc123");

        assertEquals(ORIGINAL_URL, result);
        assertEquals(1, url.getClickCount());
        verify(repository).save(url);
    }

    @Test
    void deveLancarExcecaoSeUrlNaoForEncontradaOuExpirada() {
        when(repository.findByShortCode("invalido")).thenReturn(Optional.empty());

        Exception e = assertThrows(IllegalArgumentException.class, () ->
                service.getOriginalUrl("invalido")
        );

        assertEquals("URL não encontrada ou expirada.", e.getMessage());
    }

    @Test
    void deveRetornarEstatisticasDaUrl() {
        Url url = new Url(1L, ORIGINAL_URL, "xyz789", LocalDateTime.now().plusDays(1), LocalDateTime.now());
        url.setClickCount(5);

        when(repository.findByShortCode("xyz789")).thenReturn(Optional.of(url));

        UrlResponse response = service.getUrlStats("xyz789");

        assertEquals("xyz789", response.getShortCode());
        assertEquals(ORIGINAL_URL, response.getOriginalUrl());
        assertEquals(5, response.getClickCount());
    }

    @Test
    void deveLancarExcecaoSeNaoEncontrarEstatistica() {
        when(repository.findByShortCode("naoExiste")).thenReturn(Optional.empty());

        Exception e = assertThrows(IllegalArgumentException.class, () ->
                service.getUrlStats("naoExiste")
        );

        assertEquals("URL não encontrada.", e.getMessage());
    }
}