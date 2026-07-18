package com.matrob.urlcondenser.url;

import com.matrob.urlcondenser.usuario.Usuario;
import com.matrob.urlcondenser.usuario.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UrlRepositoryTest {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deveria retornar a lista correta de URLs associadas a um usuário logado")
    void deveriaRetornarApenasUrlsDoUsuarioLogado() {
        // GIVEN
        Usuario usuario1 = new Usuario(null, "usuarioum", "senha123");
        Usuario usuario2 = new Usuario(null, "usuariodois", "senha456");

        usuarioRepository.save(usuario1);
        usuarioRepository.save(usuario2);

        Url url1 = Url.builder()
                .originalUrl("https://google.com")
                .shortCode("goog12")
                .usuario(usuario1)
                .build();

        Url url2 = Url.builder()
                .originalUrl("https://github.com")
                .shortCode("git123")
                .usuario(usuario1)
                .build();

        Url url3 = Url.builder()
                .originalUrl("https://youtube.com")
                .shortCode("yt1234")
                .usuario(usuario1)
                .build();

        Url url4 = Url.builder()
                .originalUrl("https://twitter.com")
                .shortCode("twit12")
                .usuario(usuario2)
                .build();

        Url url5 = Url.builder()
                .originalUrl("https://linkedin.com")
                .shortCode("link12")
                .usuario(usuario2)
                .build();

        urlRepository.saveAll(List.of(url1, url2, url3, url4, url5));

        // WHEN
        List<Url> urlsDoUsuario1 = urlRepository.findAllByUsuario(usuario1);
        List<Url> urlsDoUsuario2 = urlRepository.findAllByUsuario(usuario2);
        long countUsuario1 = urlRepository.countByUsuario(usuario1);
        long countUsuario2 = urlRepository.countByUsuario(usuario2);

        // THEN
        assertThat(urlsDoUsuario1)
                .hasSize(3)
                .containsExactlyInAnyOrder(url1, url2, url3);

        assertThat(urlsDoUsuario2)
                .hasSize(2)
                .containsExactlyInAnyOrder(url4, url5);

        assertThat(countUsuario1).isEqualTo(3L);
        assertThat(countUsuario2).isEqualTo(2L);
    }
}
