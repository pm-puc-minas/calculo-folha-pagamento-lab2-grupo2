package com.rh.folhaPagamento;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.strategy.CalculoIRRF;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Nome da classe ajustado para a convenção "Test"
class CalculoIRRFTest {

    // Passo 3: Adicionar uma instância do Logger
    private static final Logger logger = LoggerFactory.getLogger(CalculoIRRFTest.class);

    private CalculoIRRF calculoIRRF;
    private final int dependentes = 2;
    private final int diasUteis = 22; // Variável mantida, embora não usada no cálculo do IRRF

    @BeforeEach
    void setUp() {
        calculoIRRF = new CalculoIRRF();
    }

    @Test
    @DisplayName("Deve ser isento de IRRF para base de cálculo na Faixa 1")
    void deveEstarIsentoParaSalarioBaseNaFaixa1(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        Funcionario funcionario = new Funcionario();
        funcionario.setSalarioBruto(new BigDecimal("2200.00"));
        funcionario.setDependentes(dependentes);
        // Base de cálculo: 2200 - (189.59 * 2) = 1820.82 (Isento)
        funcionario.setDescontoINSS(new BigDecimal("0.00"));

        BigDecimal resultado = calculoIRRF.calcular(funcionario, diasUteis);

        // Passo 1: Verificar valor fixo pré-calculado
        BigDecimal esperado = new BigDecimal("0.00");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Calcula IRRF para base de cálculo na Faixa 2 (7.5%)")
    void deveCalcularIRRFCorretamenteParaFaixa2(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        Funcionario funcionario = new Funcionario();
        funcionario.setSalarioBruto(new BigDecimal("2500.00"));
        funcionario.setDependentes(dependentes);
        funcionario.setDescontoINSS(new BigDecimal("187.50"));

        BigDecimal resultado = calculoIRRF.calcular(funcionario, diasUteis);

        // Passo 1: Verificar valor fixo pré-calculado
        // Base: 2500 - 187.50 - (189.59 * 2) = 1933.32
        // IRRF: (1933.32 * 0.075) - 142.80 = 2.20
        BigDecimal esperado = new BigDecimal("2.20");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Calcula IRRF para base de cálculo na Faixa 3 (15%)")
    void deveCalcularIRRFCorretamenteParaFaixa3(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        Funcionario funcionario = new Funcionario();
        funcionario.setSalarioBruto(new BigDecimal("3500.00"));
        funcionario.setDependentes(dependentes);
        funcionario.setDescontoINSS(new BigDecimal("262.50"));

        BigDecimal resultado = calculoIRRF.calcular(funcionario, diasUteis);

        // Passo 1: Verificar valor fixo pré-calculado
        // Base: 3500 - 262.50 - (189.59 * 2) = 2858.32
        // IRRF: (2858.32 * 0.15) - 354.80 = 73.95
        BigDecimal esperado = new BigDecimal("73.95");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Calcula IRRF para base de cálculo na Faixa 4 (22.5%)")
    void deveCalcularIRRFCorretamenteParaFaixa4(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        Funcionario funcionario = new Funcionario();
        funcionario.setSalarioBruto(new BigDecimal("4500.00"));
        funcionario.setDependentes(dependentes);
        funcionario.setDescontoINSS(new BigDecimal("337.50"));

        BigDecimal resultado = calculoIRRF.calcular(funcionario, diasUteis);

        // Passo 1: Verificar valor fixo pré-calculado
        // Base: 4500 - 337.50 - (189.59 * 2) = 3783.32
        // IRRF: (3783.32 * 0.225) - 636.13 = 215.12
        BigDecimal esperado = new BigDecimal("215.12");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Calcula IRRF para base de cálculo na Faixa 5 (27.5%)")
    void deveCalcularIRRFCorretamenteParaFaixa5(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        Funcionario funcionario = new Funcionario();
        funcionario.setSalarioBruto(new BigDecimal("6000.00"));
        funcionario.setDependentes(dependentes);
        funcionario.setDescontoINSS(new BigDecimal("450.00"));

        BigDecimal resultado = calculoIRRF.calcular(funcionario, diasUteis);

        // Passo 1: Verificar valor fixo pré-calculado
        // Base: 6000 - 450 - (189.59 * 2) = 5170.82
        // IRRF: (5170.82 * 0.275) - 869.36 = 552.62
        BigDecimal esperado = new BigDecimal("552.62");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Deve retornar zero se a base de cálculo for muito baixa")
    void deveRetornarZeroSeCalculoForNegativo(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        Funcionario funcionario = new Funcionario();
        funcionario.setSalarioBruto(new BigDecimal("2000.00"));
        funcionario.setDependentes(dependentes);
        // INSS alto para forçar uma base de cálculo baixa
        funcionario.setDescontoINSS(new BigDecimal("500.00"));

        BigDecimal resultado = calculoIRRF.calcular(funcionario, diasUteis);

        // Passo 1: Verificar valor fixo pré-calculado
        // Base: 2000 - 500 - (189.59 * 2) = 1120.82 (Isento)
        BigDecimal esperado = new BigDecimal("0.00");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }
}