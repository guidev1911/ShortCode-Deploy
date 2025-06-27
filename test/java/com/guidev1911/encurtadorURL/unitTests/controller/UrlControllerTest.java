package com.guidev1911.encurtadorURL.unitTests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guidev1911.encurtadorURL.controller.UrlController;
import com.guidev1911.encurtadorURL.dto.UrlRequest;
import com.guidev1911.encurtadorURL.dto.UrlResponse;
import com.guidev1911.encurtadorURL.model.Url;
import com.guidev1911.encurtadorURL.service.UrlService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCriarEncurtadorComSucesso() throws Exception {

        UrlRequest request = new UrlRequest();
        request.setOriginalUrl("https://exemplo.com");
        request.setExpirationDate(LocalDateTime.of(2025, 12, 31, 23, 59));

        Url url = new Url();
        url.setShortCode("abc123");

        Mockito.when(urlService.createShortUrl(request.getOriginalUrl(), request.getExpirationDate()))
                .thenReturn(url);

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").value("localhost:8080/abc123"));
    }
    @Test
    void deveRedirecionarQuandoShortCodeExiste() throws Exception {
        String shortCode = "abc123";
        String originalUrl = "https://exemplo.com";

        Mockito.when(urlService.getOriginalUrl(shortCode)).thenReturn(originalUrl);

        mockMvc.perform(get("/" + shortCode))
                .andExpect(status().isFound()) // HTTP 302
                .andExpect(header().string("Location", originalUrl));
    }
    @Test
    void deveRetornar404QuandoShortCodeNaoEncontrado() throws Exception {
        String shortCode = "invalido";

        Mockito.when(urlService.getOriginalUrl(shortCode))
                .thenThrow(new IllegalArgumentException("URL não encontrada ou expirada."));

        mockMvc.perform(get("/" + shortCode))
                .andExpect(status().isNotFound()) // HTTP 404
                .andExpect(content().string("URL não encontrada ou expirada."));
    }
    @Test
    void deveRetornarStatsQuandoShortCodeExiste() throws Exception {
        String shortCode = "49e465";

        UrlResponse urlResponse = new UrlResponse();
        urlResponse.setShortCode(shortCode);
        urlResponse.setOriginalUrl("seila");
        urlResponse.setClickCount(2);
        urlResponse.setCreatedAt(LocalDateTime.of(2025, 5, 24, 20, 47, 26, 505_807_000));
        urlResponse.setExpirationDate(LocalDateTime.of(2025, 5, 25, 20, 47, 26, 505_807_000));

        Mockito.when(urlService.getUrlStats(shortCode)).thenReturn(urlResponse);

        mockMvc.perform(get("/stats/" + shortCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value(shortCode))
                .andExpect(jsonPath("$.originalUrl").value("seila"))
                .andExpect(jsonPath("$.clickCount").value(2))
                .andExpect(jsonPath("$.createdAt").value("2025-05-24T20:47:26.505807"))
                .andExpect(jsonPath("$.expirationDate").value("2025-05-25T20:47:26.505807"));
    }
    @Test
    void deveRetornar404QuandoStatsNaoEncontrado() throws Exception {
        String shortCode = "invalido";

        Mockito.when(urlService.getUrlStats(shortCode))
                .thenThrow(new IllegalArgumentException("URL não encontrada."));

        mockMvc.perform(get("/stats/" + shortCode))
                .andExpect(status().isNotFound())
                .andExpect(content().string("URL não encontrada."));
    }

}