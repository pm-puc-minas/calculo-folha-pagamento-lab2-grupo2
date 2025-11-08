package com.rh.folhaPagamento;

import com.rh.folhaPagamento.dto.FuncionarioRequestDTO;
import com.rh.folhaPagamento.event.FuncionarioCriadoEvent;
import com.rh.folhaPagamento.model.FolhaDePagamento;
import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.model.Usuario;
import com.rh.folhaPagamento.repository.FolhaPagamentoRepository;
import com.rh.folhaPagamento.repository.FuncionarioRepository;
import com.rh.folhaPagamento.repository.UsuarioRepository;
import com.rh.folhaPagamento.service.folhaPagamentoService.DetalheCalculo;
import com.rh.folhaPagamento.service.FuncionarioService;
import com.rh.folhaPagamento.service.folhaPagamentoService;

// ADICIONADO: Import do novo serviço
import com.rh.folhaPagamento.service.ArquivoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {


    @InjectMocks
    private FuncionarioService funcionarioService;


    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private FolhaPagamentoRepository folhaPagamentoRepository;

    @Mock
    private folhaPagamentoService folhaService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    // ADICIONADO: Mock para a nova dependência de serialização
    @Mock
    private ArquivoService arquivoService;

    private DetalheCalculo detalheCalculoMock;

    // SETUP INICIAL: Executado antes de CADA teste
    @BeforeEach
    void setUp() {

        detalheCalculoMock = new DetalheCalculo(
                new BigDecimal("5000.00"), // Salário Bruto
                new BigDecimal("100.00"),  // Total Adicionais
                new BigDecimal("50.00"),   // Total Benefícios
                new BigDecimal("1000.00"), // Total Descontos
                new BigDecimal("4150.00")  // Salário Líquido
        );
    }


    private FuncionarioRequestDTO criarFuncionarioRequestDTO() {
        FuncionarioRequestDTO dto = new FuncionarioRequestDTO();
        dto.setNome("João Silva");
        dto.setCpf("123.456.789-00");
        dto.setLogin("joao.silva");
        dto.setSalarioBase(new BigDecimal("5000.00"));
        dto.setCargo("Desenvolvedor");
        dto.setValeTransporte(true);
        dto.setDiasUteis(22);
        return dto;
    }


    private Funcionario criarFuncionario(Integer id, String nome, String cargo) {
        Funcionario f = new Funcionario();
        f.setId(id);
        f.setNome(nome);
        f.setCargo(cargo);
        f.setUsuario(new Usuario());
        return f;
    }

    // TESTE PRINCIPAL: Método criarFuncionario()
    @Test
    @DisplayName("Teste 1: Deve criar Funcionario, Folha, persistir e disparar Evento com sucesso")
    void criarFuncionario_Sucesso() {

        FuncionarioRequestDTO dto = criarFuncionarioRequestDTO();


        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(1);
            return u;
        });

        // CORRIGIDO: Adicionado anyInt() para mes e ano
        when(folhaService.calcularFolha(any(Funcionario.class), anyInt(), anyInt(), anyInt())).thenReturn(detalheCalculoMock);

        when(funcionarioRepository.save(any(Funcionario.class))).thenAnswer(invocation -> {
            Funcionario f = invocation.getArgument(0);
            f.setId(10);
            return f;
        });


        Funcionario resultado = funcionarioService.criarFuncionario(dto);


        assertNotNull(resultado.getId());
        assertEquals("12345678900", resultado.getCpf(), "CPF deve ser salvo apenas com dígitos.");


        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(usuarioRepository, times(1)).flush(); // Verifica o flush

        // CORRIGIDO: Adicionado anyInt() para mes e ano
        verify(folhaService, times(1)).calcularFolha(any(Funcionario.class), eq(22), anyInt(), anyInt());
        verify(funcionarioRepository, times(1)).save(any(Funcionario.class));


        ArgumentCaptor<FolhaDePagamento> folhaCaptor = ArgumentCaptor.forClass(FolhaDePagamento.class);
        verify(folhaPagamentoRepository, times(1)).save(folhaCaptor.capture());

        FolhaDePagamento folhaSalva = folhaCaptor.getValue();
        assertEquals(new BigDecimal("5000.00"), folhaSalva.getSalarioBruto());
        assertEquals(new BigDecimal("4150.00"), folhaSalva.getSalarioLiquido());


        verify(eventPublisher, times(1)).publishEvent(any(FuncionarioCriadoEvent.class));

        // ADICIONADO: Verifica se a serialização foi chamada
        verify(arquivoService, times(1)).serializar(any(Funcionario.class), any(String.class));
    }

    // TESTE DE VALIDAÇÃO: Verifica a exceção para salário base nulo
    @Test
    @DisplayName("Teste 2: Deve lançar exceção se salarioBase for nulo")
    void criarFuncionario_SalarioBaseNulo_Excecao() {

        FuncionarioRequestDTO dto = criarFuncionarioRequestDTO();
        dto.setSalarioBase(null);


        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            funcionarioService.criarFuncionario(dto);
        });

        assertEquals("salarioBase obrigatório", exception.getMessage());


        verify(usuarioRepository, never()).save(any());
        verify(funcionarioRepository, never()).save(any());
    }

    // TESTE DE BUSCA: Testa o método buscarPorLogin
    @Test
    @DisplayName("Teste 3: Deve buscar funcionário por login com sucesso")
    void buscarPorLogin_Encontrado() {

        String login = "user.test";
        Funcionario funcionarioMock = criarFuncionario(2, "Alice", "Analista");
        when(funcionarioRepository.findByUsuario_Login(login)).thenReturn(Optional.of(funcionarioMock));


        Optional<Funcionario> resultado = funcionarioService.buscarPorLogin(login);


        assertTrue(resultado.isPresent());
        assertEquals("Alice", resultado.get().getNome());
    }

    // TESTE DE LISTAGEM: Testa o método listarTodos
    @Test
    @DisplayName("Teste 4: Deve retornar a lista completa de todos os funcionários")
    void listarTodos_Sucesso() {

        Funcionario f1 = criarFuncionario(1, "Func 1", "Cargo A");
        Funcionario f2 = criarFuncionario(2, "Func 2", "Cargo B");
        List<Funcionario> listaMock = Arrays.asList(f1, f2);
        when(funcionarioRepository.findAll()).thenReturn(listaMock);


        List<Funcionario> resultado = funcionarioService.listarTodos();


        assertEquals(2, resultado.size());
        verify(funcionarioRepository, times(1)).findAll();
    }

    // TESTE DE MAPA: Testa o método getFuncionariosMap (Streams e Map)
    @Test
    @DisplayName("Teste 5: Deve retornar Map de funcionários com ID como chave")
    void getFuncionariosMap_Sucesso() {

        Funcionario f1 = criarFuncionario(101, "Func 1", "Cargo X");
        Funcionario f2 = criarFuncionario(202, "Func 2", "Cargo Y");
        List<Funcionario> listaMock = Arrays.asList(f1, f2);
        when(funcionarioRepository.findAll()).thenReturn(listaMock);


        Map<Integer, Funcionario> resultado = funcionarioService.getFuncionariosMap();


        assertEquals(2, resultado.size());
        assertTrue(resultado.containsKey(101));
        assertEquals("Func 2", resultado.get(202).getNome());
    }

    // TESTE DE FILTRO: Testa o método filtrarPorCargo (Streams e Filter)
    @Test
    @DisplayName("Teste 6: Deve filtrar funcionários por substring no cargo (case-insensitive)")
    void filtrarPorCargo_FiltrarSubstring() {
        // ARRANGE
        Funcionario f1 = criarFuncionario(1, "Alice", "Desenvolvedor Java");
        Funcionario f2 = criarFuncionario(2, "Bob", "Analista de Dados");
        Funcionario f3 = criarFuncionario(3, "Charlie", "Desenvolvedor Front");
        List<Funcionario> listaMock = Arrays.asList(f1, f2, f3);
        when(funcionarioRepository.findAll()).thenReturn(listaMock);

        // ACT
        List<Funcionario> resultado = funcionarioService.filtrarPorCargo("desenvolvedor");

        // ASSERT
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(f -> f.getNome().equals("Alice")));
        assertFalse(resultado.stream().anyMatch(f -> f.getNome().equals("Bob")));
    }
}