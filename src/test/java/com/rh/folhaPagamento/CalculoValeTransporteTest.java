package com.rh.folhaPagamento;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.calculation.CalculoValeTransporte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculoValeTransporteTest {

    // Passo 3: Adicionar uma instância do Logger
    private static final Logger logger = LoggerFactory.getLogger(CalculoValeTransporteTest.class);

    private CalculoValeTransporte calculoVT;

    @BeforeEach
    void setUp() {
        calculoVT = new CalculoValeTransporte();
    }

    @Test
    @DisplayName("Desconta 6% do salário base quando este valor é menor que o benefício total")
    void deveCalcularDescontoComLimiteDeSeisPorCento(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        Funcionario funcionario = new Funcionario();
        funcionario.setSalarioBase(new BigDecimal("2000.00"));
        funcionario.setValorVT(new BigDecimal("10.00"));
        int diasUteis = 20; // Benefício total = 200.00, Limite 6% = 120.00

        BigDecimal resultado = calculoVT.calcular(funcionario, diasUteis);

        // Passo 1: Verificar valor fixo pré-calculado (2000.00 * 0.06 = 120.00)
        BigDecimal esperado = new BigDecimal("120.00");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Desconta o valor total do benefício quando este é menor que 6% do salário base")
    void deveCalcularDescontoComValorTotalDoBeneficio(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        Funcionario funcionario = new Funcionario();
        funcionario.setSalarioBase(new BigDecimal("2000.00"));
        funcionario.setValorVT(new BigDecimal("5.00"));
        int diasUteis = 20; // Benefício total = 100.00, Limite 6% = 120.00

        BigDecimal resultado = calculoVT.calcular(funcionario, diasUteis);

        // Passo 1: Verificar valor fixo pré-calculado (5.00 * 20 = 100.00)
        BigDecimal esperado = new BigDecimal("100.00");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Retorna zero se o valor diário do VT for zero")
    void deveRetornarZeroSeValorVTForZero(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        Funcionario funcionario = new Funcionario();
        funcionario.setSalarioBase(new BigDecimal("2000.00"));
        funcionario.setValorVT(BigDecimal.ZERO);
        int diasUteis = 20;

        BigDecimal resultado = calculoVT.calcular(funcionario, diasUteis);

        // Passo 1: Verificar valor fixo
        BigDecimal esperado = new BigDecimal("0.00");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }
}