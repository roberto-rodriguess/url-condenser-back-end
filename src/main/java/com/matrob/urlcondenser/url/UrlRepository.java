package com.matrob.urlcondenser.url;
import com.matrob.urlcondenser.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByShortCode(String shortCode);

    boolean existsByOriginalUrlAndUsuario(String originalUrl, Usuario usuario);

    List<Url> findAllByUsuario(Usuario usuario);

    Optional<Url> findByIdAndUsuario(Long id, Usuario usuario);

    Optional<Url> findByShortCodeAndUsuario(String shortCode, Usuario usuario);

    long countByUsuario(Usuario usuario);

}
