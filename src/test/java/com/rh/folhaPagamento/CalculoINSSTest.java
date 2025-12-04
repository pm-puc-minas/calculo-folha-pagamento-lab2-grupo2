package com.rh.folhaPagamento;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.strategy.CalculoINSS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testes Unitários para validar o cálculo de Desconto do INSS (Progressivo).
 */
class CalculoINSSTest {

    // Passo 3: Adicionar uma instância do Logger
    private static final Logger logger = LoggerFactory.getLogger(CalculoINSSTest.class);

    private CalculoINSS calculoINSS;
    private final int DIAS_UTEIS_IGNORADO = 0; // O cálculo do INSS não usa dias úteis

    @BeforeEach
    void setUp() {
        calculoINSS = new CalculoINSS();
    }

    private Funcionario criarFuncionarioComSalario(String salario) {
        Funcionario f = new Funcionario();
        f.setSalarioBase(new BigDecimal(salario));
        return f;
    }

    @Test
    @DisplayName("Calcula INSS para Salário na Faixa 1 (7.5%)")
    void deveCalcularINSSCorretamenteParaFaixa1(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        // Salário: 1000.00
        Funcionario f = criarFuncionarioComSalario("1000.00");

        BigDecimal resultado = calculoINSS.calcular(f, DIAS_UTEIS_IGNORADO);

        // Passo 1: Verificar um valor fixo pré-calculado (1000.00 * 7.5% = 75.00)
        BigDecimal esperado = new BigDecimal("75.00");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Calcula INSS para Salário na Faixa 2 (9%)")
    void deveCalcularINSSCorretamenteParaFaixa2(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        // Salário: 2000.00
        Funcionario f = criarFuncionarioComSalario("2000.00");

        BigDecimal resultado = calculoINSS.calcular(f, DIAS_UTEIS_IGNORADO);

        // Passo 1: Verificar um valor fixo pré-calculado (2000 * 9% - 19.53 = 160.47)
        BigDecimal esperado = new BigDecimal("160.47");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Calcula INSS para Salário na Faixa 3 (12%)")
    void deveCalcularINSSCorretamenteParaFaixa3(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        // Salário: 3000.00
        Funcionario f = criarFuncionarioComSalario("3000.00");

        BigDecimal resultado = calculoINSS.calcular(f, DIAS_UTEIS_IGNORADO);

        // Passo 1: Verificar um valor fixo pré-calculado (3000 * 12% - 96.67 = 263.33)
        BigDecimal esperado = new BigDecimal("263.33");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Calcula INSS para Salário na Faixa 4 (14%)")
    void deveCalcularINSSCorretamenteParaFaixa4(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        // Salário: 7000.00
        Funcionario f = criarFuncionarioComSalario("7000.00");

        BigDecimal resultado = calculoINSS.calcular(f, DIAS_UTEIS_IGNORADO);

        // Passo 1: Verificar um valor fixo pré-calculado (7000 * 14% - 173.81 = 806.19)
        BigDecimal esperado = new BigDecimal("806.19");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Aplica o Teto do INSS para Salários Altos")
    void deveCalcularTetoParaSalarioSuperiorAoTeto(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());
        // Salário: 15000.00 (Acima do teto)
        Funcionario f = criarFuncionarioComSalario("15000.00");

        BigDecimal resultado = calculoINSS.calcular(f, DIAS_UTEIS_IGNORADO);

        // Passo 1: Verificar o valor fixo do teto (877.24)
        BigDecimal esperado = new BigDecimal("877.24");
        assertEquals(esperado, resultado);
        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }
}