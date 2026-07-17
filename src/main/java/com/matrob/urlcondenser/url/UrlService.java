package com.matrob.urlcondenser.url;

import com.matrob.urlcondenser.url.dto.UrlRequestDTO;
import com.matrob.urlcondenser.url.dto.UrlResponseDTO;
import com.matrob.urlcondenser.url.dto.UrlStatsDTO;
import com.matrob.urlcondenser.infra.exception.DuplicateUrlException;
import com.matrob.urlcondenser.infra.exception.UrlNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlService {

    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final SecureRandom random = new SecureRandom();

    private final UrlRepository repository;
    private final UrlMapper mapper;

    /**
     * Cria uma URL encurtada.
     */
    public UrlResponseDTO createShortUrl(UrlRequestDTO dto) {

        if (repository.existsByOriginalUrl(dto.originalUrl())) {
            throw new DuplicateUrlException("Essa URL já foi cadastrada.");
        }

        String shortCode = generateUniqueCode();

        Url url = Url.builder()
                .originalUrl(dto.originalUrl())
                .shortCode(shortCode)
                .build();

        repository.save(url);

        return mapper.toResponseDTO(url);
    }

    /**
     * Retorna todas as URLs cadastradas.
     */
    public List<Url> findAll() {
        return repository.findAll();
    }

    /**
     * Busca pelo ID.
     */
    public Url findById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new UrlNotFoundException("URL não encontrada."));
    }

    /**
     * Busca estatísticas.
     */
    public UrlStatsDTO getStats(String shortCode) {

        Url url = repository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new UrlNotFoundException("Código não encontrado."));

        return mapper.toStatsDTO(url);
    }

    /**
     * Remove uma URL.
     */
    public void delete(Long id) {

        Url url = findById(id);

        repository.delete(url);
    }

    /**
     * Incrementa os acessos.
     */
    public String redirect(String shortCode) {

        Url url = repository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new UrlNotFoundException("Código não encontrado."));

        url.setClicks(url.getClicks() + 1);

        repository.save(url);

        return url.getOriginalUrl();
    }

    /**
     * Gera um código curto único.
     */
    private String generateUniqueCode() {

        String code;

        do {
            code = randomCode();
        } while (repository.findByShortCode(code).isPresent());

        return code;
    }

    /**
     * Gera um código aleatório Base62.
     */
    private String randomCode() {

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 6; i++) {

            builder.append(
                    BASE62.charAt(random.nextInt(BASE62.length()))
            );

        }

        return builder.toString();
    }

}
