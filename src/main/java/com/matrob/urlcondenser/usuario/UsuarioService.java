package com.matrob.urlcondenser.usuario;

import com.matrob.urlcondenser.infra.exception.DuplicateLoginException;
import com.matrob.urlcondenser.usuario.dto.DadosAutenticacao;
import com.matrob.urlcondenser.usuario.dto.DadosDetalhamentoUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public DadosDetalhamentoUsuario cadastrar(DadosAutenticacao dados) {
        if (repository.findByLogin(dados.login()) != null) {
            throw new DuplicateLoginException("Este login já está cadastrado.");
        }

        String senhaCriptografada = passwordEncoder.encode(dados.senha());

        Usuario usuario = new Usuario(null, dados.login(), senhaCriptografada);
        repository.save(usuario);

        return new DadosDetalhamentoUsuario(usuario.getId(), usuario.getLogin());
    }

}
