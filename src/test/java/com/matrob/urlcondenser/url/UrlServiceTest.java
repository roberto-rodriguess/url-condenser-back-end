package com.matrob.urlcondenser.url;

import com.matrob.urlcondenser.infra.exception.UserUrlLimitExceededException;
import com.matrob.urlcondenser.url.dto.UrlRequestDTO;
import com.matrob.urlcondenser.usuario.Role;
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
    private UrlRepository urlRepository;

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

    @Test
    @DisplayName("Deveria retornar todas as URLs de todos os usuarios para perfil ADMIN")
    void deveriaRetornarTodasAsUrlsParaAdmin() {
        // GIVEN
        Usuario user1 = new Usuario(null, "user_one", "senha1");
        Usuario user2 = new Usuario(null, "user_two", "senha2");
        Usuario admin = new Usuario(null, "admin_user", "senha3", Role.ADMIN);
        usuarioRepository.save(user1);
        usuarioRepository.save(user2);
        usuarioRepository.save(admin);

        Url url1 = Url.builder().originalUrl("https://link1.com").shortCode("code1").usuario(user1).build();
        Url url2 = Url.builder().originalUrl("https://link2.com").shortCode("code2").usuario(user2).build();
        urlRepository.save(url1);
        urlRepository.save(url2);

        // WHEN & THEN
        var urlsAdmin = urlService.findAll(admin);
        assertThat(urlsAdmin).hasSize(2);

        var urlsUser1 = urlService.findAll(user1);
        assertThat(urlsUser1).hasSize(1);
    }

    @Test
    @DisplayName("Deveria permitir perfil ADMIN deletar url de outro usuario")
    void deveriaPermitirAdminDeletarUrlDeOutroUsuario() {
        // GIVEN
        Usuario user1 = new Usuario(null, "user_one", "senha1");
        Usuario admin = new Usuario(null, "admin_user", "senha3", Role.ADMIN);
        usuarioRepository.save(user1);
        usuarioRepository.save(admin);

        Url url = Url.builder().originalUrl("https://link.com").shortCode("code").usuario(user1).build();
        urlRepository.save(url);

        // WHEN
        urlService.delete(url.getId(), admin);

        // THEN
        assertThat(urlRepository.findById(url.getId())).isEmpty();
    }
}
