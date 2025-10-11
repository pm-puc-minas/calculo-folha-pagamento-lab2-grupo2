// TEST
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

/** * Testes Unitários para validar a lógica do Adicional de Insalubridade
 * utilizando um logger profissional (SLF4J + Logback).
 */
@DisplayName("Testes do Cálculo de Adicional de Insalubridade")
public class CalculoInsalubridadeTest {

    // 1. Crie uma instância estática e final do Logger para a classe
    private static final Logger logger = LoggerFactory.getLogger(CalculoInsalubridadeTest.class);

    private CalculoInsalubridade calculoInsalubridade;

    @BeforeEach
    void setUp() {
        calculoInsalubridade = new CalculoInsalubridade();
    }

    private Funcionario criarFuncionarioComGrau(int grau) {
        Funcionario f = new Funcionario();
        f.setGrauInsalubridade(grau);
        return f;
    }

    @Test
    @DisplayName("Deve calcular 10% para o grau mínimo")
    void deveCalcularGrauMinimoDe10PorCento(TestInfo testInfo) { // 2. Parâmetro TestInfo injetado
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName()); // 3. Log de início do teste

        Funcionario f = criarFuncionarioComGrau(1);
        BigDecimal resultado = calculoInsalubridade.calcular(f);
        BigDecimal esperado = new BigDecimal("151.80");

        assertEquals(esperado, resultado);

        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName()); // 4. Log de sucesso
    }

    @Test
    @DisplayName("Deve calcular 20% para o grau médio")
    void deveCalcularGrauMedioDe20PorCento(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());

        Funcionario f = criarFuncionarioComGrau(2);
        BigDecimal resultado = calculoInsalubridade.calcular(f);
        BigDecimal esperado = new BigDecimal("303.60");

        assertEquals(esperado, resultado);

        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Deve calcular 40% para o grau máximo")
    void deveCalcularGrauMaximoDe40PorCento(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());

        Funcionario f = criarFuncionarioComGrau(3);
        BigDecimal resultado = calculoInsalubridade.calcular(f);
        BigDecimal esperado = new BigDecimal("607.20");

        assertEquals(esperado, resultado);

        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Deve retornar zero para um grau inválido")
    void deveCalcularZeroParaGrauInvalido(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());

        Funcionario f = criarFuncionarioComGrau(99);
        BigDecimal resultado = calculoInsalubridade.calcular(f);
        BigDecimal esperado = new BigDecimal("0.00");

        assertEquals(esperado, resultado);

        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Deve retornar zero para o grau zero")
    void deveCalcularZeroParaGrauZero(TestInfo testInfo) {
        logger.info("INICIANDO TESTE: {}", testInfo.getDisplayName());

        Funcionario f = criarFuncionarioComGrau(0);
        BigDecimal resultado = calculoInsalubridade.calcular(f);
        BigDecimal esperado = new BigDecimal("0.00");

        assertEquals(esperado, resultado);

        logger.info("TESTE CONCLUÍDO COM SUCESSO: {}", testInfo.getDisplayName());
    }
}