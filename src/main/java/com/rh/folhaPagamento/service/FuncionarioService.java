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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigDecimal;

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
        if (dto.getSalarioBase() == null) {
            throw new IllegalArgumentException("salarioBase obrigatório");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin(dto.getLogin());
        novoUsuario.setSenha(dto.getSenha());
        novoUsuario.setPermissao(dto.getPermissao());
        Usuario usuarioS = usuarioRepository.save(novoUsuario);
        usuarioRepository.flush();

        Funcionario f = new Funcionario();
        f.setNome(dto.getNome());
        String cpfDigits = dto.getCpf() != null ? dto.getCpf().replaceAll("\\D", "") : null;
        f.setCpf(cpfDigits);
        f.setCargo(dto.getCargo());
        f.setDependentes(dto.getDependentes());
        f.setSalarioBase(dto.getSalarioBase());
        f.setAptoPericulosidade(Boolean.TRUE.equals(dto.getAptoPericulosidade()));
        f.setGrauInsalubridade(dto.getGrauInsalubridade());

        boolean vt = Boolean.TRUE.equals(dto.getValeTransporte());
        boolean va = Boolean.TRUE.equals(dto.getValeAlimentacao());
        f.setValeTransporte(vt);
        f.setValeAlimentacao(va);
        f.setValorVT(vt ? (dto.getValorVT() != null ? dto.getValorVT() : BigDecimal.ZERO) : BigDecimal.ZERO);
        f.setValorVA(va ? (dto.getValorVA() != null ? dto.getValorVA() : BigDecimal.ZERO) : BigDecimal.ZERO);

        f.setUsuario(usuarioS);

        int diasUteis = dto.getDiasUteis() != null ? dto.getDiasUteis() : 22;
        DetalheCalculo det = folhaService.calcularFolha(f, diasUteis);
        Funcionario salvo = funcionarioRepository.save(f);
        funcionarioRepository.flush();

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
        folhaPagamentoRepository.flush();
        return salvo;
    }

    public Optional<Funcionario> buscarPorLogin(String login){
        return funcionarioRepository.findByUsuario_Login(login);
    }

    //Retorna a lista completa de todos os funcionários.

    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAll();
    }


    //Utiliza Java Streams para filtrar funcionários por uma substring no cargo.

    public List<Funcionario> filtrarPorCargo(String cargo) {

        List<Funcionario> todosFuncionarios = funcionarioRepository.findAll();

        // Verifica se o termo de busca é nulo ou vazio
        if (cargo == null || cargo.trim().isEmpty()) {
            return todosFuncionarios;
        }

        final String termoBusca = cargo.trim().toLowerCase();

        return todosFuncionarios.stream()
                .filter(f -> f.getCargo() != null && f.getCargo().toLowerCase().contains(termoBusca))
                .collect(Collectors.toList());
    }
}