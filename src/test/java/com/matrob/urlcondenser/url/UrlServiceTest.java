package com.matrob.urlcondenser.url;

import com.matrob.urlcondenser.infra.exception.UserUrlLimitExceededException;
import com.matrob.urlcondenser.url.dto.UrlRequestDTO;
import com.matrob.urlcondenser.usuario.Usuario;
import com.matrob.urlcondenser.usuario.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UrlServiceTest {

    @Autowired
    private UrlService urlService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deveria permitir encurtar ate 10 URLs e lancar excecao no 11º link")
    void deveriaLancarExcecaoAoExcederLimiteDeDezUrls() {
        // GIVEN
        Usuario usuario = new Usuario(null, "usuario_limite", "senha123");
        usuarioRepository.save(usuario);

        // WHEN: Cadastrar 10 URLs com sucesso
        for (int i = 1; i <= 10; i++) {
            UrlRequestDTO dto = new UrlRequestDTO("https://exemplo" + i + ".com");
            var response = urlService.createShortUrl(dto, usuario);
            assertThat(response).isNotNull();
            assertThat(response.shortUrl()).isNotEmpty();
        }

        // THEN: Tentar cadastrar a 11ª URL deve lançar erro
        UrlRequestDTO dto11 = new UrlRequestDTO("https://exemplo11.com");

        assertThrows(UserUrlLimitExceededException.class, () -> {
            urlService.createShortUrl(dto11, usuario);
        });
    }
}
