package com.rh.folhaPagamento.service;

import com.rh.folhaPagamento.dto.FuncionarioRequestDTO;
import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.model.Usuario;
import com.rh.folhaPagamento.repository.FuncionarioRepository;
import com.rh.folhaPagamento.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FuncionarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Transactional
    public Funcionario criarFuncionario(FuncionarioRequestDTO dto) {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin(dto.getLogin());
        novoUsuario.setSenha(dto.getSenha());
        novoUsuario.setPermissao(dto.getPermissao());

        Usuario usuarioS = usuarioRepository.save(novoUsuario);

        Funcionario novoFuncionario = new Funcionario();
        novoFuncionario.setNome(dto.getNome());
        novoFuncionario.setCpf(dto.getCpf());
        novoFuncionario.setCargo(dto.getCargo());
        novoFuncionario.setDependentes(dto.getDependentes());
        novoFuncionario.setSalarioBase(dto.getSalarioBase());
        novoFuncionario.setAptoPericulosidade(dto.isAptoPericulosidade());
        novoFuncionario.setGrauInsalubridade(dto.getGrauInsalubridade());
        novoFuncionario.setValeTransporte(dto.isValeTransporte());
        novoFuncionario.setValorVT(dto.getValorVT());
        novoFuncionario.setValeAlimentacao(dto.isValeAlimentacao());
        novoFuncionario.setValorVA(dto.getValorVA());

        novoFuncionario.setUsuario(usuarioS);

        return funcionarioRepository.save(novoFuncionario);
    }
}
