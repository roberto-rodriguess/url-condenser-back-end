package com.matrob.urlcondenser.url;

import com.matrob.urlcondenser.url.dto.UrlRequestDTO;
import com.matrob.urlcondenser.url.dto.UrlResponseDTO;
import com.matrob.urlcondenser.url.dto.UrlStatsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor

@Tag(
        name = "URL Condenser",
        description = "Endpoints para gerenciamento de URLs."
)

public class UrlController {

    private final UrlService service;

    /**
     * Cria uma URL curta.
     */
    @Operation(summary = "Criar URL encurtada")
    @PostMapping("/api/urls")
    public ResponseEntity<UrlResponseDTO> create(
            @Valid @RequestBody UrlRequestDTO dto) {

        UrlResponseDTO response = service.createShortUrl(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Lista todas as URLs.
     */
    @Operation(summary = "Listar todas as URLs")
    @GetMapping("/api/urls")
    public ResponseEntity<List<Url>> findAll() {

        return ResponseEntity.ok(service.findAll());

    }

    /**
     * Busca URL pelo ID.
     */
    @Operation(summary = "Buscar URL por ID")
    @GetMapping("/api/urls/{id}")
    public ResponseEntity<Url> findById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.findById(id));

    }

    /**
     * Estatísticas.
     */
    @Operation(summary = "Consultar estatísticas")
    @GetMapping("/api/urls/stats/{shortCode}")
    public ResponseEntity<UrlStatsDTO> stats(
            @PathVariable String shortCode) {

        return ResponseEntity.ok(service.getStats(shortCode));

    }

    /**
     * Exclui uma URL.
     */
    @Operation(summary = "Excluir URL")
    @DeleteMapping("/api/urls/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();

    }

    /**
     * Redireciona para a URL original.
     */
    @Operation(summary = "Redirecionar URL")
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode) {

        String url = service.redirect(shortCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);

    }

}