package com.guidev1911.encurtadorURL.unitTests.service;

import com.guidev1911.encurtadorURL.repository.UrlRepository;
import com.guidev1911.encurtadorURL.service.UrlServiceValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UrlServiceValidationTest {

    private UrlRepository repository;
    private UrlServiceValidation validation;

    @BeforeEach
    void setUp() {
        repository = mock(UrlRepository.class);
        validation = new UrlServiceValidation();
        validation.repository = repository;
    }

    @Test
    void generateRandomCode_DeveGerarCodigoDe6Caracteres() {
        String code = validation.generateRandomCode();
        assertNotNull(code);
        assertEquals(6, code.length());
        assertTrue(code.matches("^[a-zA-Z]{6}$"));
    }

    @Test
    void generateUniqueShortCode_DeveRetornarCodigoUnico() {
        when(repository.existsByShortCode(anyString())).thenReturn(false);

        String code = validation.generateUniqueShortCode();

        assertNotNull(code);
        assertEquals(6, code.length());
        verify(repository, atLeastOnce()).existsByShortCode(code);
    }

    @Test
    void generateUniqueShortCode_DeveRepetirGeracaoSeExistente() {
        when(repository.existsByShortCode(anyString()))
                .thenReturn(true)
                .thenReturn(false);

        String code = validation.generateUniqueShortCode();

        assertNotNull(code);
        assertEquals(6, code.length());
        verify(repository, atLeast(2)).existsByShortCode(anyString());
    }

    @Test
    void generateUniqueShortCode_DeveLancarExcecaoSeNaoGerarCodigoUnico() {
        when(repository.existsByShortCode(anyString())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> validation.generateUniqueShortCode());

        assertEquals("Falha ao gerar um código curto único após várias tentativas.", ex.getMessage());
        verify(repository, times(10)).existsByShortCode(anyString());
    }

    @Test
    void isValidUrl_DeveRetornarTrueParaUrlValida() {
        String url = "https://www.google.com";
        assertTrue(validation.isValidUrl(url));
    }

    @Test
    void isValidUrl_DeveRetornarFalseParaUrlComProtocoloInvalido() {
        String url = "ftp://www.google.com";
        assertFalse(validation.isValidUrl(url));
    }

    @Test
    void isValidUrl_DeveRetornarFalseParaStringMalformada() {
        String url = "ht!tp:/invalid-url";
        assertFalse(validation.isValidUrl(url));
    }

    @Test
    void isValidUrl_DeveRetornarFalseParaNullOuVazia() {
        assertFalse(validation.isValidUrl(null));
        assertFalse(validation.isValidUrl(""));
    }
}