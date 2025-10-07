package com.rh.folhaPagamento.service.calculation;

import com.rh.folhaPagamento.model.Funcionario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculoVTTeste {

    private CalculoVT calculoVT;

    @BeforeEach
    void setUp() {
        calculoVT = new CalculoVT();
    }

    @Test
    void deveCalcularDescontoComLimiteDeSeisPorCento() {

        Funcionario funcionario = new Funcionario();
        funcionario.setSalarioBase(new BigDecimal("2000.00"));
        funcionario.setValorVT(new BigDecimal("10.00"));
        int diasUteis = 20;

        BigDecimal resultado = calculoVT.calcular(funcionario, diasUteis);

        BigDecimal limiteDesconto = new BigDecimal("2000.00").multiply(new BigDecimal("0.06"));
        assertEquals(limiteDesconto.setScale(2), resultado);
    }

    @Test
    void deveCalcularDescontoComValorTotalDoBeneficio() {

        Funcionario funcionario = new Funcionario();
        funcionario.setSalarioBase(new BigDecimal("2000.00"));
        funcionario.setValorVT(new BigDecimal("5.00"));
        int diasUteis = 20;

        BigDecimal resultado = calculoVT.calcular(funcionario, diasUteis);

        BigDecimal valorTotalBeneficio = new BigDecimal("5.00").multiply(new BigDecimal(diasUteis));
        assertEquals(valorTotalBeneficio.setScale(2), resultado);
    }

    @Test
    void deveRetornarZeroSeValorVTForZero() {

        Funcionario funcionario = new Funcionario();
        funcionario.setSalarioBase(new BigDecimal("2000.00"));
        funcionario.setValorVT(BigDecimal.ZERO);
        int diasUteis = 20;

        BigDecimal resultado = calculoVT.calcular(funcionario, diasUteis);

        BigDecimal esperado = BigDecimal.ZERO;

        assertEquals(esperado.setScale(2), resultado);
    }
}