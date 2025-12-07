package com.rh.folhaPagamento;
import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.calculation.*;
import com.rh.folhaPagamento.service.folhaPagamentoService;
import com.rh.folhaPagamento.service.folhaPagamentoService.DetalheCalculo;

// ADICIONADO: Import do novo serviço que precisa ser mockado
import com.rh.folhaPagamento.service.ArquivoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FolhaPagamentoServiceTest {

    // O objeto real a ser testado
    @InjectMocks
    private folhaPagamentoService service;

    // Mocks para simular os resultados das classes de cálculo
    @Mock private CalculoInsalubridade calculoInsalubridade;
    @Mock private CalculoPericulosidade calculoPericulosidade;
    @Mock private CalculoValeAlimentacao calculoVA;
    @Mock private CalculoValeTransporte calculoVT;
    @Mock private CalculoINSS calculoINSS;
    @Mock private CalculoIRRF calculoIRRF;

    // ADICIONADO: Mock para a nova dependência de serialização
    @Mock private ArquivoService arquivoService;


    private Funcionario funcionario;
    private final int DIAS_UTEIS = 22;
    private final BigDecimal SALARIO_BASE_TESTE = new BigDecimal("3000.00");
    private final int MES_TESTE = 11;
    private final int ANO_TESTE = 2025;

    @BeforeEach
    void setUp() {
        funcionario = new Funcionario();
        funcionario.setSalarioBase(SALARIO_BASE_TESTE);
        funcionario.setDependentes(0);
        funcionario.setAptoPericulosidade(false);
        funcionario.setGrauInsalubridade(0);
        funcionario.setValeTransporte(false);
        funcionario.setValeAlimentacao(false);
        funcionario.setValorVT(BigDecimal.ZERO);
        funcionario.setValorVA(BigDecimal.ZERO);
    }

    @Test
    void deveCalcularFolhaCorretamente_CenarioBase() {
        // CENÁRIO: Sem adicionais, sem benefícios. Apenas Descontos obrigatórios.

        // Mocks de Descontos (INSS e IRRF)
        when(calculoINSS.calcular(any(Funcionario.class), anyInt())).thenReturn(new BigDecimal("250.00"));
        when(calculoIRRF.calcular(any(Funcionario.class), anyInt())).thenReturn(new BigDecimal("150.00"));

        /* CÁLCULO ESPERADO */
        // Salário Bruto: 3000.00
        // Total Descontos: 250.00 (INSS) + 150.00 (IRRF) = 400.00
        // Total a Pagar: 3000.00 - 400.00 = 2600.00

        BigDecimal resultadoEsperado = new BigDecimal("2600.00").setScale(2, RoundingMode.HALF_UP);

        // CORRIGIDO: Adicionado mes e ano na chamada
        DetalheCalculo r = service.calcularFolha(funcionario, DIAS_UTEIS, MES_TESTE, ANO_TESTE);
        BigDecimal resultadoAtual = r.totalAPagar;

        assertEquals(resultadoEsperado, resultadoAtual, "O cálculo total no cenário base falhou.");
    }

    @Test
    void deveIncluirAdicionalPericulosidade_e_Beneficios() {
        funcionario.setAptoPericulosidade(true);
        funcionario.setValeTransporte(true);
        funcionario.setValeAlimentacao(true);

        when(calculoPericulosidade.calcular(any(Funcionario.class))).thenReturn(new BigDecimal("900.00"));
        when(calculoINSS.calcular(any(Funcionario.class), anyInt())).thenReturn(new BigDecimal("450.00"));
        when(calculoIRRF.calcular(any(Funcionario.class), anyInt())).thenReturn(new BigDecimal("300.00"));
        when(calculoVT.calcular(any(Funcionario.class), anyInt())).thenReturn(new BigDecimal("180.00"));
        when(calculoVA.calcular(any(Funcionario.class), anyInt())).thenReturn(new BigDecimal("440.00"));

        BigDecimal resultadoEsperado = new BigDecimal("3410.00").setScale(2, RoundingMode.HALF_UP);

        DetalheCalculo r = service.calcularFolha(funcionario, DIAS_UTEIS, MES_TESTE, ANO_TESTE);
        BigDecimal resultadoAtual = r.totalAPagar;

        assertEquals(resultadoEsperado, resultadoAtual, "O cálculo com adicionais e benefícios falhou.");
    }
}