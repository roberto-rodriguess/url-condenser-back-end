package com.matrob.urlcondenser.url;

import com.matrob.urlcondenser.url.dto.UrlRequestDTO;
import com.matrob.urlcondenser.url.dto.UrlResponseDTO;
import com.matrob.urlcondenser.url.dto.UrlStatsDTO;
import com.matrob.urlcondenser.infra.exception.DuplicateUrlException;
import com.matrob.urlcondenser.infra.exception.UrlNotFoundException;
import com.matrob.urlcondenser.infra.exception.UserUrlLimitExceededException;
import com.matrob.urlcondenser.usuario.Role;
import com.matrob.urlcondenser.usuario.Usuario;
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
    public UrlResponseDTO createShortUrl(UrlRequestDTO dto, Usuario usuario) {

        if (repository.countByUsuario(usuario) >= 10) {
            throw new UserUrlLimitExceededException("Você atingiu o limite máximo de 10 URLs.");
        }

        if (repository.existsByOriginalUrlAndUsuario(dto.originalUrl(), usuario)) {
            throw new DuplicateUrlException("Você já cadastrou essa URL.");
        }

        String shortCode = generateUniqueCode();

        Url url = Url.builder()
                .originalUrl(dto.originalUrl())
                .shortCode(shortCode)
                .usuario(usuario)
                .build();

        repository.save(url);

        return mapper.toResponseDTO(url);
    }

    /**
     * Retorna todas as URLs cadastradas para o usuário logado.
     */
    public List<Url> findAll(Usuario usuario) {
        if (usuario.getRole() == Role.ADMIN) {
            return repository.findAll();
        }
        return repository.findAllByUsuario(usuario);
    }

    /**
     * Busca pelo ID e pelo usuário.
     */
    public Url findById(Long id, Usuario usuario) {
        if (usuario.getRole() == Role.ADMIN) {
            return repository.findById(id)
                    .orElseThrow(() -> new UrlNotFoundException("URL não encontrada."));
        }
        return repository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() ->
                        new UrlNotFoundException("URL não encontrada."));
    }

    /**
     * Busca estatísticas pelo código e usuário.
     */
    public UrlStatsDTO getStats(String shortCode, Usuario usuario) {

        Url url = repository.findByShortCodeAndUsuario(shortCode, usuario)
                .orElseThrow(() ->
                        new UrlNotFoundException("Código não encontrado ou não pertence a você."));

        return mapper.toStatsDTO(url);
    }

    /**
     * Remove uma URL do usuário.
     */
    public void delete(Long id, Usuario usuario) {

        Url url = findById(id, usuario);

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
