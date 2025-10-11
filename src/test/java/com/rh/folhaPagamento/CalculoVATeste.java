package com.rh.folhaPagamento;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.calculation.CalculoVA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculoVATeste {

    private CalculoVA calculoVA;

    @BeforeEach
    void setUp() {
        calculoVA = new CalculoVA();
    }

    @Test
    void deveCalcularValeAlimentacaoCorretamente() {

        Funcionario funcionario = new Funcionario();
        BigDecimal valorVaDiario = new BigDecimal("100.00");
        funcionario.setValorVA(valorVaDiario);

        int diasUteis = 20;

        BigDecimal resultado = new BigDecimal("2500.00");

        BigDecimal esperado = valorVaDiario.multiply(new BigDecimal(diasUteis));

        assertEquals(esperado.setScale(2), resultado);
    }

    @Test
    void deveRetornarZeroSeValorVAForZero() {
        Funcionario funcionario = new Funcionario();
        funcionario.setValorVA(BigDecimal.ZERO);
        int diasUteis = 20;

        BigDecimal resultado = calculoVA.calcular(funcionario, diasUteis);

        BigDecimal esperado = BigDecimal.ZERO;

        assertEquals(esperado.setScale(2), resultado);
    }
}