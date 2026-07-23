package com.matrob.urlcondenser.url;

import com.matrob.urlcondenser.url.dto.UrlRequestDTO;
import com.matrob.urlcondenser.url.dto.UrlResponseDTO;
import com.matrob.urlcondenser.url.dto.UrlStatsDTO;
import com.matrob.urlcondenser.usuario.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.matrob.urlcondenser.infra.exception.UrlNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @Value("${frontend.url}")
    private String frontendUrl;

    /**
     * Cria uma URL curta.
     */
    @Operation(summary = "Criar URL encurtada")
    @PostMapping("/api/urls")
    public ResponseEntity<UrlResponseDTO> create(
            @Valid @RequestBody UrlRequestDTO dto,
            @AuthenticationPrincipal Usuario usuario) {

        UrlResponseDTO response = service.createShortUrl(dto, usuario);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Lista todas as URLs.
     */
    @Operation(summary = "Listar todas as URLs")
    @GetMapping("/api/urls")
    public ResponseEntity<List<Url>> findAll(@AuthenticationPrincipal Usuario usuario) {

        return ResponseEntity.ok(service.findAll(usuario));

    }

    /**
     * Busca URL pelo ID.
     */
    @Operation(summary = "Buscar URL por ID")
    @GetMapping("/api/urls/{id}")
    public ResponseEntity<Url> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        return ResponseEntity.ok(service.findById(id, usuario));

    }

    /**
     * Estatísticas.
     */
    @Operation(summary = "Consultar estatísticas")
    @GetMapping("/api/urls/stats/{shortCode}")
    public ResponseEntity<UrlStatsDTO> stats(
            @PathVariable String shortCode,
            @AuthenticationPrincipal Usuario usuario) {

        return ResponseEntity.ok(service.getStats(shortCode, usuario));

    }

    /**
     * Exclui uma URL.
     */
    @Operation(summary = "Excluir URL")
    @DeleteMapping("/api/urls/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        service.delete(id, usuario);

        return ResponseEntity.noContent().build();

    }

    /**
     * Redireciona para a URL original.
     */
    @Operation(summary = "Redirecionar URL")
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode) {

        HttpHeaders headers = new HttpHeaders();
        try {
            String url = service.redirect(shortCode);
            headers.setLocation(URI.create(url));
        } catch (UrlNotFoundException ex) {
            String redirectUrl = frontendUrl.endsWith("/") ? frontendUrl + "link-invalido.html" : frontendUrl + "/link-invalido.html";
            headers.setLocation(URI.create(redirectUrl));
        }

        return new ResponseEntity<>(headers, HttpStatus.FOUND);

    }

}