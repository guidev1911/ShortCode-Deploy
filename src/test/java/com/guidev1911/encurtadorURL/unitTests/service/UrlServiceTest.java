package com.guidev1911.encurtadorURL.unitTests.service;

import static org.junit.jupiter.api.Assertions.*;
import com.guidev1911.encurtadorURL.dto.UrlResponse;
import com.guidev1911.encurtadorURL.model.Url;
import com.guidev1911.encurtadorURL.repository.UrlRepository;
import com.guidev1911.encurtadorURL.service.UrlService;
import com.guidev1911.encurtadorURL.service.UrlServiceValidation;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.ZonedDateTime;
import java.util.Optional;
import static org.mockito.Mockito.*;
import com.guidev1911.encurtadorURL.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import java.time.ZoneId;
import static org.mockito.ArgumentMatchers.any;


class UrlServiceTest {

    @InjectMocks
    private UrlService service;

    @Mock
    private UrlRepository repository;

    @Mock
    private UrlServiceValidation validation;

    private final ZoneId zoneId = ZoneId.of("America/Sao_Paulo");

    private final String ORIGINAL_URL = "https://exemplo.com";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCriarShortUrlComDataPadrao() {
        when(validation.isValidUrl(ORIGINAL_URL)).thenReturn(true);
        when(validation.generateUniqueShortCode()).thenReturn("abc123");

        ZonedDateTime now = ZonedDateTime.now(zoneId);
        Url mockUrl = new Url(1L, ORIGINAL_URL, "abc123", now, now.plusDays(1));
        when(repository.save(any())).thenReturn(mockUrl);

        Url result = service.createShortUrl(ORIGINAL_URL, null);

        assertNotNull(result);
        assertEquals(ORIGINAL_URL, result.getOriginalUrl());
        assertEquals("abc123", result.getShortCode());
        verify(repository).save(any(Url.class));
    }

    @Test
    void deveLancarEmptyUrlExceptionQuandoUrlForNulaOuVazia() {
        EmptyUrlException e1 = assertThrows(EmptyUrlException.class, () -> service.createShortUrl(null, null));
        assertEquals("A URL original não pode estar vazia.", e1.getMessage());

        EmptyUrlException e2 = assertThrows(EmptyUrlException.class, () -> service.createShortUrl("   ", null));
        assertEquals("A URL original não pode estar vazia.", e2.getMessage());
    }

    @Test
    void deveLancarInvalidUrlFormatExceptionQuandoUrlForInvalida() {
        when(validation.isValidUrl(ORIGINAL_URL)).thenReturn(false);

        InvalidUrlFormatException e = assertThrows(InvalidUrlFormatException.class, () -> service.createShortUrl(ORIGINAL_URL, null));
        assertEquals("A URL fornecida é inválida ou potencialmente maliciosa.", e.getMessage());
    }

    @Test
    void deveLancarExpirationDateInPastExceptionQuandoDataExpirada() {
        when(validation.isValidUrl(ORIGINAL_URL)).thenReturn(true);
        ZonedDateTime passada = ZonedDateTime.now(zoneId).minusDays(1);

        ExpirationDateInPastException e = assertThrows(ExpirationDateInPastException.class,
                () -> service.createShortUrl(ORIGINAL_URL, passada));
        assertEquals("A data de expiração não pode estar no passado.", e.getMessage());
    }

    @Test
    void deveLancarExpirationDateExceedsLimitExceptionQuandoDataMuitoFutura() {
        when(validation.isValidUrl(ORIGINAL_URL)).thenReturn(true);
        ZonedDateTime muitoFutura = ZonedDateTime.now(zoneId).plusDays(8);

        ExpirationDateExceedsLimitException e = assertThrows(ExpirationDateExceedsLimitException.class,
                () -> service.createShortUrl(ORIGINAL_URL, muitoFutura));
        assertEquals("A data de expiração não pode exceder 7 dias.", e.getMessage());
    }

    @Test
    void deveRetornarUrlOriginalEIncrementarClick() {
        String shortCode = "abc123";
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        Url url = new Url(1L, ORIGINAL_URL, shortCode, now, now.plusDays(1));
        url.setClickCount(0);

        when(repository.findByShortCode(shortCode)).thenReturn(Optional.of(url));
        when(repository.save(any())).thenReturn(url);

        String result = service.getOriginalUrl(shortCode);

        assertEquals(ORIGINAL_URL, result);
        assertEquals(1, url.getClickCount());
        verify(repository).save(url);
    }

    @Test
    void deveLancarEmptyUrlExceptionQuandoShortCodeParaGetOriginalUrlForNuloOuVazio() {
        EmptyUrlException e1 = assertThrows(EmptyUrlException.class, () -> service.getOriginalUrl(null));
        assertEquals("A URL encurtada não pode estar vazia.", e1.getMessage());

        EmptyUrlException e2 = assertThrows(EmptyUrlException.class, () -> service.getOriginalUrl(" "));
        assertEquals("A URL encurtada não pode estar vazia.", e2.getMessage());
    }

    @Test
    void deveLancarUrlNotFoundExceptionSeUrlNaoExistirOuExpirada() {
        String shortCode = "invalido";
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        when(repository.findByShortCode(shortCode)).thenReturn(Optional.empty());

        UrlNotFoundException e = assertThrows(UrlNotFoundException.class, () -> service.getOriginalUrl(shortCode));
        assertEquals("URL não encontrada ou expirada.", e.getMessage());

        // Também testar filtro de expiração
        Url expirada = new Url(1L, ORIGINAL_URL, shortCode, now.minusDays(10), now.minusDays(1));
        when(repository.findByShortCode(shortCode)).thenReturn(Optional.of(expirada));

        e = assertThrows(UrlNotFoundException.class, () -> service.getOriginalUrl(shortCode));
        assertEquals("URL não encontrada ou expirada.", e.getMessage());
    }

    @Test
    void deveRetornarUrlStatsComSucesso() {
        String shortCode = "xyz789";
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        Url url = new Url(1L, ORIGINAL_URL, shortCode, now, now.plusDays(1));
        url.setClickCount(5);

        when(repository.findByShortCode(shortCode)).thenReturn(Optional.of(url));

        UrlResponse response = service.getUrlStats(shortCode);

        assertEquals(shortCode, response.getShortCode());
        assertEquals(ORIGINAL_URL, response.getOriginalUrl());
        assertEquals(5, response.getClickCount());
        assertEquals(url.getCreatedAt(), response.getCreatedAt());
        assertEquals(url.getExpirationDate(), response.getExpirationDate());
    }

    @Test
    void deveLancarEmptyUrlExceptionQuandoShortCodeParaGetUrlStatsForNuloOuVazio() {
        EmptyUrlException e1 = assertThrows(EmptyUrlException.class, () -> service.getUrlStats(null));
        assertEquals("O código da URL não pode estar vazio.", e1.getMessage());

        EmptyUrlException e2 = assertThrows(EmptyUrlException.class, () -> service.getUrlStats(" "));
        assertEquals("O código da URL não pode estar vazio.", e2.getMessage());
    }

    @Test
    void deveLancarUrlNotFoundExceptionSeShortCodeParaGetUrlStatsNaoExistir() {
        String shortCode = "invalido";

        when(repository.findByShortCode(shortCode)).thenReturn(Optional.empty());

        UrlNotFoundException e = assertThrows(UrlNotFoundException.class, () -> service.getUrlStats(shortCode));
        assertEquals("URL não encontrada.", e.getMessage());
    }
}