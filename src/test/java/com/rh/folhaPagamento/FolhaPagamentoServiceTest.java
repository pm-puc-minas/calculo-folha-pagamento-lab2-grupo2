package com.rh.folhaPagamento.service;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.calculation.*;
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
    @Mock private CalculoVA calculoVA;
    @Mock private CalculoVT calculoVT;
    @Mock private CalculoINSS calculoINSS;
    @Mock private CalculoIRRF calculoIRRF;


    private Funcionario funcionario;
    private final int DIAS_UTEIS = 22;
    private final BigDecimal SALARIO_BASE_TESTE = new BigDecimal("3000.00");

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

        BigDecimal resultadoAtual = service.calcularFolha(funcionario, DIAS_UTEIS);

        assertEquals(resultadoEsperado, resultadoAtual, "O cálculo total no cenário base falhou.");
    }

    @Test
    void deveIncluirAdicionalPericulosidade_e_Beneficios() {
        // CENÁRIO: Com Periculosidade, VT e VA

        funcionario.setAptoPericulosidade(true);
        funcionario.setValeTransporte(true);
        funcionario.setValeAlimentacao(true);

        // Mocks de Valores
        when(calculoPericulosidade.calcular(any(Funcionario.class))).thenReturn(new BigDecimal("900.00")); // Adicional (30% de 3000)
        when(calculoINSS.calcular(any(Funcionario.class), anyInt())).thenReturn(new BigDecimal("450.00")); // INSS simulado
        when(calculoIRRF.calcular(any(Funcionario.class), anyInt())).thenReturn(new BigDecimal("300.00")); // IRRF simulado
        when(calculoVT.calcular(any(Funcionario.class), anyInt())).thenReturn(new BigDecimal("180.00")); // Desconto VT (6% de 3000)
        when(calculoVA.calcular(any(Funcionario.class), anyInt())).thenReturn(new BigDecimal("440.00")); // Benefício VA

        /* CÁLCULO ESPERADO */
        // Salário Bruto = 3000.00 + 900.00 (Periculosidade) = 3900.00

        // Total Descontos = 450.00 (INSS) + 300.00 (IRRF) + 180.00 (VT) = 930.00

        // Total Benefícios = 440.00 (VA)

        // Salário Líquido = 3900.00 - 930.00 = 2970.00

        // Total a Pagar = 2970.00 (Líquido) + 440.00 (Benefício) = 3410.00

        BigDecimal resultadoEsperado = new BigDecimal("3410.00").setScale(2, RoundingMode.HALF_UP);

        BigDecimal resultadoAtual = service.calcularFolha(funcionario, DIAS_UTEIS);

        assertEquals(resultadoEsperado, resultadoAtual, "O cálculo com adicionais e benefícios falhou.");
    }
}
