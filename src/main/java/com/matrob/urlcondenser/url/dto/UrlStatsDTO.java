package com.matrob.urlcondenser.url.dto;

import java.time.LocalDateTime;

public record UrlStatsDTO(

        Long id,

        String originalUrl,

        String shortCode,

        Long clicks,

        LocalDateTime createdAt

) {
}
