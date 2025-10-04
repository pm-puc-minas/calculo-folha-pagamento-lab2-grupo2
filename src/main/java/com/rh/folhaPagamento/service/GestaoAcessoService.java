package com.rh.folhaPagamento.service;

import com.rh.folhaPagamento.model.Usuario;
import com.rh.folhaPagamento.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GestaoAcessoService {
    private final UsuarioRepository usuarioRepository;

    public GestaoAcessoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public void auth(String Login, String Senha){
        Optional<Usuario> usuarioOpt = usuarioRepository.findByLogin(Login);

        if(usuarioOpt.isEmpty()){
            throw new RuntimeException("Usuário não encontrado");
        }
        Usuario usuario = usuarioOpt.get();

        if(!Senha.equals(usuario.getSenha())){
            throw new RuntimeException("Senha incorreta");
        }
    }
}
