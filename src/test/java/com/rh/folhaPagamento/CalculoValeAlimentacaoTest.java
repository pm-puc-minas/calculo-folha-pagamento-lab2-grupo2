package com.rh.folhaPagamento;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.calculation.CalculoValeAlimentacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Nome da classe ajustado para a convenção "Test"
class CalculoValeAlimentacaoTest {

    // Passo 3: Adicionar uma instância do Logger
    private static final Logger logger = LoggerFactory.getLogger(CalculoValeAlimentacaoTest.class);

    private CalculoVA calculoVA;

    @BeforeEach
    void setUp() {
        calculoVA = new CalculoVA();
    }

    @Test
    @DisplayName("Calcula o valor total do Vale Alimentação com base nos dias úteis")
    void deveCalcularValeAlimentacaoCorretamente(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        Funcionario funcionario = new Funcionario();
        BigDecimal valorVaDiario = new BigDecimal("100.00");
        funcionario.setValorVA(valorVaDiario);

        int diasUteis = 20;

        BigDecimal resultado = calculoVA.calcular(funcionario, diasUteis);

        // Passo 1: Verificar valor fixo pré-calculado (100.00 * 20 = 2000.00)
        BigDecimal esperado = new BigDecimal("2000.00");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Retorna zero como valor total do VA se o valor diário for zero")
    void deveRetornarZeroSeValorVAForZero(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        Funcionario funcionario = new Funcionario();
        funcionario.setValorVA(BigDecimal.ZERO);
        int diasUteis = 20;

        BigDecimal resultado = calculoVA.calcular(funcionario, diasUteis);

        // Passo 1: Verificar valor fixo
        BigDecimal esperado = new BigDecimal("0.00");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }
}