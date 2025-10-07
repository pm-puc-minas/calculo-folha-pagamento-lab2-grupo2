package com.rh.folhaPagamento;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.calculation.CalculoInsalubridade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculoInsalubridadeTeste {

    private CalculoInsalubridade calculoInsalubridade;
    private final BigDecimal salarioMinimo = new BigDecimal("1518.00");

    @BeforeEach
    void setUp() {
        calculoInsalubridade = new CalculoInsalubridade();
    }

    @Test
    void deveCalcularInsalubridadeGrau10() {

        Funcionario funcionario = new Funcionario();
        funcionario.setGrauInsalubridade(1);

        BigDecimal resultado = calculoInsalubridade.calcular(funcionario);

        BigDecimal esperado = salarioMinimo.multiply(new BigDecimal("0.10"));
        assertEquals(esperado.setScale(2), resultado);
    }

    @Test
    void deveCalcularInsalubridadeGrau20() {

        Funcionario funcionario = new Funcionario();
        funcionario.setGrauInsalubridade(2);

        BigDecimal resultado = calculoInsalubridade.calcular(funcionario);

        BigDecimal esperado = salarioMinimo.multiply(new BigDecimal("0.20"));
        assertEquals(esperado.setScale(2), resultado);
    }

    @Test
    void deveCalcularInsalubridadeGrau40() {

        Funcionario funcionario = new Funcionario();
        funcionario.setGrauInsalubridade(3);

        BigDecimal resultado = calculoInsalubridade.calcular(funcionario);

        BigDecimal esperado = salarioMinimo.multiply(new BigDecimal("0.40"));
        assertEquals(esperado.setScale(2), resultado);
    }

    @Test
    void deveCalcularZeroParaGrauInvalido() {

        Funcionario funcionario = new Funcionario();
        funcionario.setGrauInsalubridade(99);

        BigDecimal resultado = calculoInsalubridade.calcular(funcionario);

        BigDecimal esperado = BigDecimal.ZERO;
        assertEquals(esperado.setScale(2), resultado);
    }

    @Test
    void deveCalcularZeroParaGrauZero() {

        Funcionario funcionario = new Funcionario();
        funcionario.setGrauInsalubridade(0);

        BigDecimal resultado = calculoInsalubridade.calcular(funcionario);

        BigDecimal esperado = BigDecimal.ZERO;
        assertEquals(esperado.setScale(2), resultado);
    }
}