package com.rh.folhaPagamento.service;

import com.rh.folhaPagamento.dto.FuncionarioRequestDTO;
import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.model.Usuario;
import com.rh.folhaPagamento.model.FolhaDePagamento;
import com.rh.folhaPagamento.repository.FuncionarioRepository;
import com.rh.folhaPagamento.repository.UsuarioRepository;
import com.rh.folhaPagamento.repository.FolhaPagamentoRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rh.folhaPagamento.service.folhaPagamentoService.DetalheCalculo;


@Service
public class FuncionarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private FolhaPagamentoRepository folhaPagamentoRepository;

    @Autowired
    private folhaPagamentoService folhaService;

    @Transactional
    public Funcionario criarFuncionario(FuncionarioRequestDTO dto) {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin(dto.getLogin());
        novoUsuario.setSenha(dto.getSenha());
        novoUsuario.setPermissao(dto.getPermissao());
        Usuario usuarioS = usuarioRepository.save(novoUsuario);

        Funcionario f = new Funcionario();
        f.setNome(dto.getNome());
        f.setCpf(dto.getCpf());
        f.setCargo(dto.getCargo());
        f.setDependentes(dto.getDependentes());
        f.setSalarioBase(dto.getSalarioBase());
        f.setAptoPericulosidade(Boolean.TRUE.equals(dto.getAptoPericulosidade()));
        f.setGrauInsalubridade(dto.getGrauInsalubridade());
        f.setValeTransporte(Boolean.TRUE.equals(dto.getValeTransporte()));
        f.setValorVT(dto.getValorVT());
        f.setValeAlimentacao(Boolean.TRUE.equals(dto.getValeAlimentacao()));
        f.setValorVA(dto.getValorVA());
        f.setUsuario(usuarioS);

        int diasUteis = dto.getDiasUteis() != null ? dto.getDiasUteis() : 22;
        DetalheCalculo det = folhaService.calcularFolha(f, diasUteis);
        Funcionario salvo = funcionarioRepository.save(f);

        java.time.LocalDate hoje = java.time.LocalDate.now();
        FolhaDePagamento fol = new FolhaDePagamento();
        fol.setFuncionario(salvo);
        fol.setMesReferencia(hoje.getMonthValue());
        fol.setAnoReferencia(hoje.getYear());
        fol.setSalarioBruto(det.salarioBruto);
        fol.setTotalAdicionais(det.totalAdicionais);
        fol.setTotalBeneficios(det.totalBeneficios);
        fol.setTotalDescontos(det.totalDescontos);
        fol.setSalarioLiquido(det.salarioLiquido);
        folhaPagamentoRepository.save(fol);
        return salvo;
    }

    public java.util.Optional<Funcionario> buscarPorLogin(String login){
        return funcionarioRepository.findByUsuario_Login(login);
    }
}
