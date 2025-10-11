package com.rh.folhaPagamento;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.calculation.CalculoPericulosidade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Nome da classe ajustado para a convenção "Test"
class CalculoPericulosidadeTest {

    // Passo 3: Adicionar uma instância do Logger
    private static final Logger logger = LoggerFactory.getLogger(CalculoPericulosidadeTest.class);

    private CalculoPericulosidade calculoPericulosidade;

    @BeforeEach
    void setUp() {
        calculoPericulosidade = new CalculoPericulosidade();
    }

    @Test
    @DisplayName("Calcula 30% do salário base quando o funcionário é apto à periculosidade")
    void deveCalcularPericulosidadeCorretamenteQuandoApto(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        Funcionario funcionario = new Funcionario();
        funcionario.setSalarioBase(new BigDecimal("1000.00"));
        funcionario.setAptoPericulosidade(true);

        BigDecimal resultado = calculoPericulosidade.calcular(funcionario);

        // Passo 1: Verificar valor fixo pré-calculado (1000.00 * 30% = 300.00)
        BigDecimal esperado = new BigDecimal("300.00");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Retorna zero quando o funcionário não é apto à periculosidade")
    void deveRetornarZeroQuandoNaoApto(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        Funcionario funcionario = new Funcionario();
        funcionario.setSalarioBase(new BigDecimal("2000.00"));
        funcionario.setAptoPericulosidade(false);

        BigDecimal resultado = calculoPericulosidade.calcular(funcionario);

        // Passo 1: Verificar valor fixo
        BigDecimal esperado = new BigDecimal("0.00");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }
}