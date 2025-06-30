package com.guidev1911.encurtadorURL.unitTests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.guidev1911.encurtadorURL.controller.UrlController;
import com.guidev1911.encurtadorURL.dto.UrlRequest;
import com.guidev1911.encurtadorURL.dto.UrlResponse;
import com.guidev1911.encurtadorURL.exceptions.UrlNotFoundException;
import com.guidev1911.encurtadorURL.exceptions.global.GlobalExceptionHandler;
import com.guidev1911.encurtadorURL.model.Url;
import com.guidev1911.encurtadorURL.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UrlControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ZoneId zoneId = ZoneId.of("America/Sao_Paulo");

    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlController urlController;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(urlController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /shorten - Deve criar shortCode com sucesso")
    void shouldCreateShortCodeSuccessfully() throws Exception {
        UrlRequest request = new UrlRequest("https://exemplo.com",
                ZonedDateTime.of(2025, 12, 31, 23, 59, 0, 0, zoneId));

        Url url = new Url(null, "https://exemplo.com", "abc123",
                ZonedDateTime.now(zoneId), ZonedDateTime.now(zoneId).plusDays(1));

        Mockito.when(urlService.createShortUrl(anyString(), any())).thenReturn(url);

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").value("scd-gowv.onrender.com/abc123"));
    }

    @Test
    @DisplayName("GET /{shortCode} - Deve redirecionar se shortCode existir")
    void shouldRedirectIfShortCodeExists() throws Exception {
        Mockito.when(urlService.getOriginalUrl("abc123"))
                .thenReturn("https://exemplo.com");

        mockMvc.perform(get("/abc123"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://exemplo.com"));
    }

    @Test
    @DisplayName("GET /{shortCode} - Deve retornar 404 se shortCode não existir")
    void shouldReturn404IfShortCodeNotFound() throws Exception {
        Mockito.when(urlService.getOriginalUrl("invalido"))
                .thenThrow(new UrlNotFoundException("URL não encontrada ou expirada."));

        mockMvc.perform(get("/invalido"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("URL não encontrada ou expirada."))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("GET /stats/{shortCode} - Deve retornar 404 se estatísticas não existirem")
    void shouldReturn404IfStatsNotFound() throws Exception {
        Mockito.when(urlService.getUrlStats("naoExiste"))
                .thenThrow(new UrlNotFoundException("URL não encontrada."));

        mockMvc.perform(get("/stats/naoExiste"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("URL não encontrada."))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("GET /stats/{shortCode} - Deve retornar estatísticas se shortCode válido")
    void shouldReturnStatsIfShortCodeValid() throws Exception {
        UrlResponse response = new UrlResponse(
                "abc123",
                "https://exemplo.com",
                10,
                ZonedDateTime.of(2025, 1, 1, 12, 0, 0, 0, zoneId),
                ZonedDateTime.of(2025, 1, 5, 12, 0, 0, 0, zoneId)
        );

        Mockito.when(urlService.getUrlStats("abc123")).thenReturn(response);

        mockMvc.perform(get("/stats/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.originalUrl").value("https://exemplo.com"))
                .andExpect(jsonPath("$.clickCount").value(10));
    }
}
