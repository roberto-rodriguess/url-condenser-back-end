package com.matrob.urlcondenser.url.dto;

import java.time.LocalDateTime;

public record UrlListResponseDTO(
        Long id,
        String originalUrl,
        String shortCode,
        String shortUrl,
        Long clicks,
        LocalDateTime createdAt,
        String criador
) {}
