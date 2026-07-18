package com.matrob.urlcondenser.usuario;

import com.matrob.urlcondenser.usuario.dto.DadosAutenticacao;
import com.matrob.urlcondenser.usuario.dto.DadosDetalhamentoUsuario;
import com.matrob.urlcondenser.usuario.dto.DadosTokenJWT;
import com.matrob.urlcondenser.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping
public class AutenticacaoController {
    private final AuthenticationManager manager;
    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    public AutenticacaoController(AuthenticationManager manager, TokenService tokenService, UsuarioService usuarioService) {
        this.manager = manager;
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<DadosTokenJWT> login(@RequestBody @Valid DadosAutenticacao dados) {
        var authenticationToken  = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
        Authentication authentication = manager.authenticate(authenticationToken);

        String tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());

        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
    }

    @PostMapping("/register")
    public ResponseEntity<DadosDetalhamentoUsuario> registrar(@RequestBody @Valid DadosAutenticacao dados, UriComponentsBuilder uriBuilder) {
        DadosDetalhamentoUsuario usuario = usuarioService.cadastrar(dados);
        var uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(usuario.id()).toUri();
        return ResponseEntity.created(uri).body(usuario);
    }
}
