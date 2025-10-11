package com.rh.folhaPagamento;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.calculation.CalculoInsalubridade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Nome da classe ajustado para a convenção "Test"
class CalculoInsalubridadeTest {

    // Passo 3: Adicionar uma instância do Logger
    private static final Logger logger = LoggerFactory.getLogger(CalculoInsalubridadeTest.class);

    private CalculoInsalubridade calculoInsalubridade;

    @BeforeEach
    void setUp() {
        calculoInsalubridade = new CalculoInsalubridade();
    }

    @Test
    @DisplayName("Calcula adicional de 10% (Grau 1) sobre o salário mínimo")
    void deveCalcularInsalubridadeGrau10(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        Funcionario funcionario = new Funcionario();
        funcionario.setGrauInsalubridade(1);

        BigDecimal resultado = calculoInsalubridade.calcular(funcionario);

        // Passo 1: Verificar valor fixo pré-calculado (1518.00 * 0.10 = 151.80)
        BigDecimal esperado = new BigDecimal("151.80");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Calcula adicional de 20% (Grau 2) sobre o salário mínimo")
    void deveCalcularInsalubridadeGrau20(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        Funcionario funcionario = new Funcionario();
        funcionario.setGrauInsalubridade(2);

        BigDecimal resultado = calculoInsalubridade.calcular(funcionario);

        // Passo 1: Verificar valor fixo pré-calculado (1518.00 * 0.20 = 303.60)
        BigDecimal esperado = new BigDecimal("303.60");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Calcula adicional de 40% (Grau 3) sobre o salário mínimo")
    void deveCalcularInsalubridadeGrau40(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        Funcionario funcionario = new Funcionario();
        funcionario.setGrauInsalubridade(3);

        BigDecimal resultado = calculoInsalubridade.calcular(funcionario);

        // Passo 1: Verificar valor fixo pré-calculado (1518.00 * 0.40 = 607.20)
        BigDecimal esperado = new BigDecimal("607.20");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Retorna zero para grau de insalubridade inválido")
    void deveCalcularZeroParaGrauInvalido(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        Funcionario funcionario = new Funcionario();
        funcionario.setGrauInsalubridade(99);

        BigDecimal resultado = calculoInsalubridade.calcular(funcionario);

        // Passo 1: Verificar valor fixo
        BigDecimal esperado = new BigDecimal("0.00");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Retorna zero para grau de insalubridade zero")
    void deveCalcularZeroParaGrauZero(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        Funcionario funcionario = new Funcionario();
        funcionario.setGrauInsalubridade(0);

        BigDecimal resultado = calculoInsalubridade.calcular(funcionario);

        // Passo 1: Verificar valor fixo
        BigDecimal esperado = new BigDecimal("0.00");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }
}