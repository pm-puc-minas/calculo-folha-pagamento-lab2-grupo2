package com.rh.folhaPagamento.service;

import com.rh.folhaPagamento.dto.FuncionarioRequestDTO;
// NOVO IMPORT: Para receber dados da requisição de ajuste salarial
import com.rh.folhaPagamento.dto.AjusteSalarialRequestDTO;
import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.model.Usuario;
import com.rh.folhaPagamento.model.FolhaDePagamento;
import com.rh.folhaPagamento.repository.FuncionarioRepository;
import com.rh.folhaPagamento.repository.UsuarioRepository;
import com.rh.folhaPagamento.repository.FolhaPagamentoRepository;
import com.rh.folhaPagamento.event.FuncionarioCriadoEvent;
// NOVO IMPORT: Importa o evento que acabamos de criar
import com.rh.folhaPagamento.event.AjusteSalarialEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rh.folhaPagamento.service.folhaPagamentoService.DetalheCalculo;

// import com.rh.folhaPagamento.service.ArquivoService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;
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

    // Injeção do Publicador de Eventos
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    // @Autowired
    private ArquivoService arquivoService;

    @Transactional
    public Funcionario criarFuncionario(FuncionarioRequestDTO dto) {
        if (dto.getSalarioBase() == null) {
            throw new IllegalArgumentException("salarioBase obrigatório");
        }

        // --- 1. Verificações de duplicidade (DA BRANCH MAIN) ---
        String login = dto.getLogin();
        String cpfDigits = dto.getCpf() != null ? dto.getCpf().replaceAll("\\D", "") : null;

        boolean loginExiste = usuarioRepository.existsByLogin(login);
        boolean cpfExiste = funcionarioRepository.existsByCpf(cpfDigits);

        if (loginExiste && cpfExiste) {
            throw new IllegalArgumentException("Login e CPF já existentes.");
        } else if (loginExiste) {
            throw new IllegalArgumentException("Login já existente: " + login);
        } else if (cpfExiste) {
            throw new IllegalArgumentException("CPF já existente: " + dto.getCpf());
        }

        // --- 2. Criação do Usuário ---
        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin(login); // <-- Usa a variável 'login'
        // A SENHA AQUI PRECISA SER CRIPTOGRAFADA na vida real (o colega deve fazer isso)
        novoUsuario.setSenha(dto.getSenha());
        novoUsuario.setPermissao(dto.getPermissao());
        Usuario usuarioS = usuarioRepository.save(novoUsuario);
        usuarioRepository.flush();

        // --- 3. Criação do Funcionário ---
        Funcionario f = new Funcionario();
        f.setNome(dto.getNome());
        f.setCpf(cpfDigits); // <-- Usa a variável 'cpfDigits'
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

        // --- 4. Cálculo da Folha (LÓGICA MESCLADA) ---
        int diasUteis = dto.getDiasUteis() != null ? dto.getDiasUteis() : 22;

        // <-- MUDANÇA DA SUA BRANCH: Pega 'hoje' para usar na nova assinatura
        java.time.LocalDate hoje = java.time.LocalDate.now();
        // <-- MUDANÇA DA SUA BRANCH: Usa a assinatura com mes/ano
        DetalheCalculo det = folhaService.calcularFolha(f, diasUteis, hoje.getMonthValue(), hoje.getYear());

        Funcionario salvo = funcionarioRepository.save(f);
        funcionarioRepository.flush();

        // --- 5. Evento de Criação ---
        FuncionarioCriadoEvent event = new FuncionarioCriadoEvent(this, salvo);
        eventPublisher.publishEvent(event);

        // --- 6. Criação da Folha de Pagamento ---
        // (A variável 'hoje' já foi declarada acima)
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

        // --- 7. SERIALIZAÇÃO DO NOVO FUNCIONÁRIO (DA SUA BRANCH) ---
        try {
            String nomeArquivo = "funcionario_" + salvo.getId() + ".dat";
            System.out.println("Serializando funcionário recém-criado em: " + nomeArquivo);
            arquivoService.serializar(salvo, nomeArquivo);
        } catch (Exception e) {
            System.err.println("Erro ao serializar funcionário: " + e.getMessage());
            // Não quebra a transação principal por um erro de serialização
        }

        return salvo;
    }

    // =========================================================================
    // MÉTODO ATUALIZAR SALÁRIO (MESCLADO)
    // =========================================================================
    @Transactional
    public Funcionario atualizarSalario(Integer id, BigDecimal novoSalario) {
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado com o ID: " + id));

        // 1. Guarda o salário antigo antes de mudar
        BigDecimal salarioAntigo = funcionario.getSalarioBase();

        // 2. Atualiza o salário base
        funcionario.setSalarioBase(novoSalario);

        // 2.1 Recalcula campos derivados (LÓGICA MESCLADA)
        // <-- MUDANÇA DA SUA BRANCH: Pega 'hoje'
        java.time.LocalDate hoje = java.time.LocalDate.now();
        // <-- MUDANÇA DA SUA BRANCH: Usa a assinatura com mes/ano
        folhaService.calcularFolha(funcionario, 22, hoje.getMonthValue(), hoje.getYear());

        // 3. Salva a alteração no banco de dados
        Funcionario salvo = funcionarioRepository.save(funcionario);

        // 4. DISPARA O NOVO EVENTO DE AJUSTE SALARIAL
        AjusteSalarialEvent event = new AjusteSalarialEvent(this, salvo, salarioAntigo, novoSalario);
        eventPublisher.publishEvent(event);

        // --- 5. SERIALIZAÇÃO DA ATUALIZAÇÃO (DA SUA BRANCH) ---
        try {
            String nomeArquivo = "funcionario_" + salvo.getId() + ".dat";
            System.out.println("Serializando atualização de salário em: " + nomeArquivo);
            arquivoService.serializar(salvo, nomeArquivo);
        } catch (Exception e) {
            System.err.println("Erro ao serializar funcionário: " + e.getMessage());
        }

        return salvo;
    }
    // =========================================================================

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
     */
    public Map<Integer, Funcionario> getFuncionariosMap() {
        List<Funcionario> todosFuncionarios = funcionarioRepository.findAll();

        // Uso de Streams e Map
        return todosFuncionarios.stream()
                .collect(Collectors.toMap(
                        Funcionario::getId,   // Chave: ID (Integer)
                        funcionario -> funcionario) // Valor: Objeto Funcionario
                );
    }


    // Utiliza Java Streams para filtrar funcionários por uma substring no cargo.
    public List<Funcionario> filtrarPorCargo(String cargo) {

        List<Funcionario> todosFuncionarios = funcionarioRepository.findAll();

        // Verifica se o termo de busca é nulo ou vazio
        if (cargo == null || cargo.trim().isEmpty()) {
            // return todosFuncionarios;
        }

        final String termoBusca = cargo.trim().toLowerCase();

        return todosFuncionarios.stream()
                .filter(f -> f.getCargo() != null && f.getCargo().toLowerCase().contains(termoBusca))
                .collect(Collectors.toList());
    }
    /**
     * Busca um funcionário serializado a partir de um arquivo .dat.
     * @param id O ID do funcionário a ser buscado.
            * @return O objeto Funcionario, ou null se não for encontrado ou der erro.
            */
    public Funcionario buscarFuncionarioDoArquivo(Integer id) {
        String nomeArquivo = "funcionario_" + id + ".dat";
        System.out.println("Tentando desserializar funcionário de: " + nomeArquivo);

        Object obj = arquivoService.desserializar(nomeArquivo);

        if (obj instanceof Funcionario) {
            return (Funcionario) obj;
        }

        System.out.println("Arquivo " + nomeArquivo + " não encontrado ou não é um Funcionario.");
        return null;
    }
}