package com.rh.folhaPagamento.service;

import com.rh.folhaPagamento.dto.FuncionarioRequestDTO;
import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.model.Usuario;
import com.rh.folhaPagamento.model.FolhaDePagamento;
import com.rh.folhaPagamento.repository.FuncionarioRepository;
import com.rh.folhaPagamento.repository.UsuarioRepository;
import com.rh.folhaPagamento.repository.FolhaPagamentoRepository;
import com.rh.folhaPagamento.event.FuncionarioCriadoEvent; // ADICIONADO: Import do Evento
import org.springframework.context.ApplicationEventPublisher; // ADICIONADO: Import do Publicador
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rh.folhaPagamento.service.folhaPagamentoService.DetalheCalculo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map; // ADICIONADO: Import do Map
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

    // ADICIONADO: Injeção do Publicador de Eventos (necessário para o requisito de Eventos)
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public Funcionario criarFuncionario(FuncionarioRequestDTO dto) {
        if (dto.getSalarioBase() == null) {
            throw new IllegalArgumentException("salarioBase obrigatório");
        }

        // --- 1. Criação do Usuário ---
        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin(dto.getLogin());
        // A SENHA AQUI PRECISA SER CRIPTOGRAFADA na vida real (o colega deve fazer isso)
        novoUsuario.setSenha(dto.getSenha());
        novoUsuario.setPermissao(dto.getPermissao());
        Usuario usuarioS = usuarioRepository.save(novoUsuario);
        usuarioRepository.flush();

        // --- 2. Criação do Funcionário ---
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

        // --- 3. Cálculo da Folha e Persistência do Funcionário ---
        int diasUteis = dto.getDiasUteis() != null ? dto.getDiasUteis() : 22;
        DetalheCalculo det = folhaService.calcularFolha(f, diasUteis);
        Funcionario salvo = funcionarioRepository.save(f);
        funcionarioRepository.flush();

        // IMPLEMENTAÇÃO DE EVENTOS: Disparo do Evento
        FuncionarioCriadoEvent event = new FuncionarioCriadoEvent(this, salvo);
        eventPublisher.publishEvent(event);
        // O LogFuncionarioListener ouvirá este evento e registrará o log.

        // --- 4. Criação da Folha de Pagamento ---
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

    // Retorna a lista completa de todos os funcionários.
    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAll();
    }

    // IMPLEMENTAÇÃO DE COLEÇÕES (Map): Requisito Sprint 3
    /**
     * Retorna todos os funcionários como um Map onde a chave é o ID (Integer).
     * Corrigido para usar Integer.
     */
    public Map<Integer, Funcionario> getFuncionariosMap() {
        List<Funcionario> todosFuncionarios = funcionarioRepository.findAll();

        // Uso de Streams e Map
        return todosFuncionarios.stream()
                .collect(Collectors.toMap(
                        Funcionario::getId,   // Chave: ID (Integer)
                        funcionario -> funcionario) // Valor: Objeto Funcionario
                ); // <--- PARÊNTESE E PONTO E VÍRGULA CORRIGIDOS
    }


    // Utiliza Java Streams para filtrar funcionários por uma substring no cargo.
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