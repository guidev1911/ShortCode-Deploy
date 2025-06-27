package com.guidev1911.encurtadorURL.controller.swagger;

import com.guidev1911.encurtadorURL.dto.UrlRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface UrlControllerDocs {

    @Operation(summary = "Cria uma URL encurtada - data de expiração é opcional")
    @ApiResponse(
            responseCode = "201",
            description = "A URL foi encurtada com sucesso"
    )
    ResponseEntity<Object> shorten(@RequestBody UrlRequest request);

    @Operation(summary = "Redireciona o link")
    @ApiResponse(
            responseCode = "200",
            description = "Redirecionamento com sucesso"
    )
    @ApiResponse(
            responseCode = "404",
            description = "URL não encontrada ou expirada"
    )
    ResponseEntity<?> redirect(@PathVariable String shortCode);

    @Operation(summary = "exibe dados da url encurtada")
    @ApiResponse(
            responseCode = "200",
            description = "URL foi encontrada e os dados foram exibidos"
    )
    @ApiResponse(
            responseCode = "404",
            description = "URL não encontrada ou expirada"
    )
    ResponseEntity<?> getStats(@PathVariable String shortCode);


}
