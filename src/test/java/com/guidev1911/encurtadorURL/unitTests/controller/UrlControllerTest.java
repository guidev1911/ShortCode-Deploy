package com.guidev1911.encurtadorURL.unitTests.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.guidev1911.encurtadorURL.controller.UrlController;
import com.guidev1911.encurtadorURL.dto.UrlRequest;
import com.guidev1911.encurtadorURL.dto.UrlResponse;
import com.guidev1911.encurtadorURL.model.Url;
import com.guidev1911.encurtadorURL.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
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

    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlController urlController;

    private final ZoneId zoneId = ZoneId.of("America/Sao_Paulo");

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
    }

    @Test
    void deveCriarShortCodeComSucesso() throws Exception {
        UrlRequest request = new UrlRequest();
        request.setOriginalUrl("https://exemplo.com");
        request.setExpirationDate(ZonedDateTime.of(2025, 12, 31, 23, 59, 0, 0, zoneId));

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
    void deveRedirecionarSeShortCodeExistir() throws Exception {
        Mockito.when(urlService.getOriginalUrl("abc123"))
                .thenReturn("https://exemplo.com");

        mockMvc.perform(get("/abc123"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://exemplo.com"));
    }

    @Test
    void deveRetornarNotFoundSeShortCodeNaoExistir() throws Exception {
        Mockito.when(urlService.getOriginalUrl("invalido"))
                .thenThrow(new IllegalArgumentException("URL não encontrada ou expirada."));

        mockMvc.perform(get("/invalido"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("URL não encontrada ou expirada."));
    }

    @Test
    void deveRetornarEstatisticasSeShortCodeValido() throws Exception {
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

    @Test
    void deveRetornarNotFoundSeEstatisticaNaoExistir() throws Exception {
        Mockito.when(urlService.getUrlStats("invalido"))
                .thenThrow(new IllegalArgumentException("URL não encontrada."));

        mockMvc.perform(get("/stats/invalido"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("URL não encontrada."));
    }
}
