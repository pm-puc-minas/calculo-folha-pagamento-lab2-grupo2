package com.rh.folhaPagamento.service;

import com.rh.folhaPagamento.model.Usuario;
import com.rh.folhaPagamento.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GestaoAcessoService {
    private final UsuarioRepository usuarioRepository;

    private final ArquivoService arquivoService;

    public GestaoAcessoService(UsuarioRepository usuarioRepository, ArquivoService arquivoService) {
        this.usuarioRepository = usuarioRepository;
        this.arquivoService = arquivoService;
    }

    public void auth(String Login, String Senha){
        authenticate(Login, Senha);
    }

    public Usuario authenticate(String Login, String Senha){
        Optional<Usuario> usuarioOpt = usuarioRepository.findByLogin(Login);
        if(usuarioOpt.isEmpty()){
            throw new RuntimeException("Usuário não encontrado");
        }
        Usuario usuario = usuarioOpt.get();
        if(!Senha.equals(usuario.getSenha())){
            throw new RuntimeException("Senha incorreta");
        }

        // --- SERIALIZAÇÃO DO USUÁRIO (ADICIONADO) ---
        // Salva o usuário em um arquivo após o login bem-sucedido
        try {
            String nomeArquivo = "usuario_" + usuario.getId() + ".dat";
            System.out.println("Serializando usuário autenticado em: " + nomeArquivo);
            arquivoService.serializar(usuario, nomeArquivo);
        } catch (Exception e) {
            System.err.println("Erro ao serializar usuário: " + e.getMessage());
            // Não quebra a autenticação por um erro de serialização
        }
        // =================================================

        return usuario;
    }

    /**
     * Busca um usuário serializado a partir de um arquivo .dat.
     * @param id O ID do usuário a ser buscado.
     * @return O objeto Usuario, ou null se não for encontrado ou der erro.
     */
    public Usuario buscarUsuarioDoArquivo(Integer id) {
        String nomeArquivo = "usuario_" + id + ".dat";
        System.out.println("Tentando desserializar usuário de: " + nomeArquivo);

        Object obj = arquivoService.desserializar(nomeArquivo);

        if (obj instanceof Usuario) {
            return (Usuario) obj;
        }

        System.out.println("Arquivo " + nomeArquivo + " não encontrado ou não é um Usuario.");
        return null;
    }
}