package com.matrob.urlcondenser.url.dto;

public record UrlResponseDTO(

        Long id,

        String originalUrl,

        String shortCode,

        String shortUrl

) {
}
