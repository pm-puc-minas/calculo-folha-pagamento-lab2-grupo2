package com.rh.folhaPagamento.service;

import com.rh.folhaPagamento.dto.FuncionarioRequestDTO;
import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.model.Usuario;
import com.rh.folhaPagamento.model.FolhaDePagamento;
import com.rh.folhaPagamento.repository.FuncionarioRepository;
import com.rh.folhaPagamento.repository.UsuarioRepository;
import com.rh.folhaPagamento.repository.FolhaPagamentoRepository;
import com.rh.folhaPagamento.event.FuncionarioCriadoEvent;
import com.rh.folhaPagamento.event.AjusteSalarialEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rh.folhaPagamento.service.folhaPagamentoService.DetalheCalculo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;
import java.math.BigDecimal;
import java.time.LocalDate;

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

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private ArquivoService arquivoService;

    @Transactional
    public Funcionario criarFuncionario(FuncionarioRequestDTO dto) {
        if (dto.getSalarioBase() == null) {
            throw new IllegalArgumentException("salarioBase obrigatório");
        }

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

        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin(login);
        // A SENHA AQUI PRECISA SER CRIPTOGRAFADA na vida real (o colega deve fazer isso)
        novoUsuario.setSenha(dto.getSenha());
        novoUsuario.setPermissao(dto.getPermissao());
        Usuario usuarioS = usuarioRepository.save(novoUsuario);
        usuarioRepository.flush();

        // --- 3. Criação do Funcionário ---
        Funcionario f = new Funcionario();
        f.setNome(dto.getNome());
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

        // --- 4. Cálculo da Folha (LÓGICA MESCLADA) ---
        int diasUteis = dto.getDiasUteis() != null ? dto.getDiasUteis() : 22;

        java.time.LocalDate hoje = java.time.LocalDate.now();
        DetalheCalculo det = folhaService.calcularFolha(f, diasUteis, hoje.getMonthValue(), hoje.getYear());

        Funcionario salvo = funcionarioRepository.save(f);
        funcionarioRepository.flush();

        // --- 5. Evento de Criação ---
        FuncionarioCriadoEvent event = new FuncionarioCriadoEvent(this, salvo);
        eventPublisher.publishEvent(event);

        // --- 6. Criação da Folha de Pagamento ---
        FolhaDePagamento fol = new FolhaDePagamento();
        fol.setFuncionario(salvo);
        fol.setMesReferencia(hoje.getMonthValue());
        fol.setAnoReferencia(hoje.getYear());
        fol.setSalarioBruto(det.salarioBruto);
        fol.setTotalAdicionais(det.totalAdicionais);
        fol.setTotalBeneficios(det.totalBeneficios);
        fol.setTotalDescontos(det.totalDescontos);
        fol.setSalarioLiquido(det.salarioLiquido);
        fol.setInsalubridade(det.insalubridade);
        fol.setPericulosidade(det.periculosidade);
        fol.setValeAlimentacao(det.valeAlimentacao);
        fol.setValeTransporte(det.valeTransporte);
        fol.setInss(det.descontoINSS);
        fol.setIrrf(det.descontoIRRF);
        folhaPagamentoRepository.save(fol);
        folhaPagamentoRepository.flush();

        // --- 7. SERIALIZAÇÃO DO NOVO FUNCIONÁRIO (DA SUA BRANCH) ---
        try {
            String nomeArquivo = "funcionario_" + salvo.getId() + ".dat";
            System.out.println("Serializando funcionário recém-criado em: " + nomeArquivo);
            arquivoService.serializar(salvo, nomeArquivo);
        } catch (Exception e) {
            System.err.println("Erro ao serializar funcionário: " + e.getMessage());
        }

        return salvo;
    }

    @Transactional
    public Funcionario atualizarSalario(Integer id, BigDecimal novoSalario) {
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado com o ID: " + id));

        BigDecimal salarioAntigo = funcionario.getSalarioBase();

        funcionario.setSalarioBase(novoSalario);

        java.time.LocalDate hoje = java.time.LocalDate.now();
        folhaService.calcularFolha(funcionario, 22, hoje.getMonthValue(), hoje.getYear());

        Funcionario salvo = funcionarioRepository.save(funcionario);

        AjusteSalarialEvent event = new AjusteSalarialEvent(this, salvo, salarioAntigo, novoSalario);
        eventPublisher.publishEvent(event);

        try {
            String nomeArquivo = "funcionario_" + salvo.getId() + ".dat";
            System.out.println("Serializando atualização de salário em: " + nomeArquivo);
            arquivoService.serializar(salvo, nomeArquivo);
        } catch (Exception e) {
            System.err.println("Erro ao serializar funcionário: " + e.getMessage());
        }

        return salvo;
    }

    public Optional<Funcionario> buscarPorLogin(String login){
        return funcionarioRepository.findByUsuario_Login(login);
    }

    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAll();
    }

    public Map<Integer, Funcionario> getFuncionariosMap() {
        List<Funcionario> todosFuncionarios = funcionarioRepository.findAll();

        return todosFuncionarios.stream()
                .collect(Collectors.toMap(
                        Funcionario::getId,   // Chave: ID (Integer)
                        funcionario -> funcionario) // Valor: Objeto Funcionario
                );
    }

    public List<Funcionario> filtrarPorCargo(String cargo) {

        List<Funcionario> todosFuncionarios = funcionarioRepository.findAll();


        final String termoBusca = cargo.trim().toLowerCase();

        return todosFuncionarios.stream()
                .filter(f -> f.getCargo() != null && f.getCargo().toLowerCase().contains(termoBusca))
                .collect(Collectors.toList());
    }

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

    public Funcionario atualizarParcial(Integer id, Map<String, Object> body) {
        Funcionario f = funcionarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado"));

        boolean precisaRecalcularFolha = false;

        if (body.containsKey("cargo")) {
            f.setCargo(String.valueOf(body.get("cargo")));
        }
        if (body.containsKey("dependentes")) {
            f.setDependentes(Integer.parseInt(String.valueOf(body.get("dependentes"))));
            precisaRecalcularFolha = true;
        }
        if (body.containsKey("salarioBase")) {
            f.setSalarioBase(new BigDecimal(String.valueOf(body.get("salarioBase"))));
            precisaRecalcularFolha = true;
        }
        if (body.containsKey("aptoPericulosidade")) {
            f.setAptoPericulosidade(Boolean.parseBoolean(String.valueOf(body.get("aptoPericulosidade"))));
            precisaRecalcularFolha = true;
        }
        if (body.containsKey("grauInsalubridade")) {
            f.setGrauInsalubridade(Integer.parseInt(String.valueOf(body.get("grauInsalubridade"))));
            precisaRecalcularFolha = true;
        }
        if (body.containsKey("valeTransporte")) {
            f.setValeTransporte(Boolean.parseBoolean(String.valueOf(body.get("valeTransporte"))));
            precisaRecalcularFolha = true;
        }
        if (body.containsKey("valeAlimentacao")) {
            f.setValeAlimentacao(Boolean.parseBoolean(String.valueOf(body.get("valeAlimentacao"))));
            precisaRecalcularFolha = true;
        }
        if (body.containsKey("valorVT")) {
            f.setValorVT(new BigDecimal(String.valueOf(body.get("valorVT"))));
            precisaRecalcularFolha = true;
        }
        if (body.containsKey("valorVA")) {
            f.setValorVA(new BigDecimal(String.valueOf(body.get("valorVA"))));
            precisaRecalcularFolha = true;
        }

        Funcionario fsalvo = funcionarioRepository.save(f);

        if (precisaRecalcularFolha) {
            int diasUteis = body.containsKey("diasUteis") ? Integer.parseInt(String.valueOf(body.get("diasUteis"))) : 22;

            LocalDate hoje = LocalDate.now();
            int mes = hoje.getMonthValue();
            int ano = hoje.getYear();

            folhaPagamentoService.DetalheCalculo det = folhaService.calcularFolha(fsalvo, diasUteis, mes, ano);

            FolhaDePagamento folha = folhaPagamentoRepository
                    .findByFuncionarioAndMesReferenciaAndAnoReferencia(fsalvo, mes, ano)
                    .orElseGet(FolhaDePagamento::new);

            folha.setFuncionario(fsalvo);
            folha.setMesReferencia(mes);
            folha.setAnoReferencia(ano);
            folha.setSalarioBruto(det.salarioBruto);
            folha.setTotalAdicionais(det.totalAdicionais);
            folha.setTotalBeneficios(det.totalBeneficios);
            folha.setTotalDescontos(det.totalDescontos);
            folha.setSalarioLiquido(det.salarioLiquido);

            folhaPagamentoRepository.save(folha);
        }

        return fsalvo;
    }
}
