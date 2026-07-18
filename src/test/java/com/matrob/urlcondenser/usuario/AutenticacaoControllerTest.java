package com.matrob.urlcondenser.usuario;

import com.matrob.urlcondenser.usuario.dto.DadosAutenticacao;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class AutenticacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deveria cadastrar novo usuario com sucesso e salvar senha criptografada")
    void deveriaCadastrarNovoUsuario() throws Exception {
        // GIVEN
        DadosAutenticacao dados = new DadosAutenticacao("novousuario", "senhaSegura123");

        // WHEN & THEN
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.login").value("novousuario"));

        Usuario usuarioSalvo = (Usuario) usuarioRepository.findByLogin("novousuario");
        assertThat(usuarioSalvo).isNotNull();
        assertThat(passwordEncoder.matches("senhaSegura123", usuarioSalvo.getPassword())).isTrue();
    }

    @Test
    @DisplayName("Deveria retornar 409 ao tentar cadastrar usuario com login duplicado")
    void deveriaRetornarConflitoParaUsuarioDuplicado() throws Exception {
        // GIVEN
        Usuario usuarioExistente = new Usuario(null, "usuarioexistente", passwordEncoder.encode("senha123"));
        usuarioRepository.save(usuarioExistente);

        DadosAutenticacao dados = new DadosAutenticacao("usuarioexistente", "outrasenha123");

        // WHEN & THEN
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deveria realizar login com sucesso e retornar token JWT")
    void deveriaLogarComSucesso() throws Exception {
        // GIVEN
        Usuario usuario = new Usuario(null, "usuarioteste", passwordEncoder.encode("senhaValida"));
        usuarioRepository.save(usuario);

        DadosAutenticacao dados = new DadosAutenticacao("usuarioteste", "senhaValida");

        // WHEN & THEN
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("Deveria retornar 403 Forbidden ao tentar logar com credenciais invalidas")
    void deveriaNegarAcessoParaLoginInvalido() throws Exception {
        // GIVEN
        DadosAutenticacao dados = new DadosAutenticacao("naoexiste", "senhainvalida");

        // WHEN & THEN
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isForbidden());
    }
}
