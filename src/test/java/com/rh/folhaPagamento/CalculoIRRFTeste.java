package com.rh.folhaPagamento;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.calculation.CalculoIRRF;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.RoundingMode;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculoIRRFTeste {

    private CalculoIRRF calculoIRRF;
    private int dependentes = 2;

    @BeforeEach
    void setUp() {
        calculoIRRF = new CalculoIRRF();
    }

    @Test
    void deveEstarIsentoParaSalarioBaseNaFaixa1() {

        BigDecimal salarioBruto = new BigDecimal("2200.00");
        BigDecimal valorINSS = new BigDecimal("165.00");

        BigDecimal resultado = calculoIRRF.calcular(salarioBruto, valorINSS, dependentes);

        assertEquals(BigDecimal.ZERO.setScale(2), resultado);
    }

    @Test
    void deveCalcularIRRFCorretamenteParaFaixa2() {

        BigDecimal salarioBruto = new BigDecimal("2500.00");
        BigDecimal valorINSS = new BigDecimal("187.50");


        BigDecimal resultado = calculoIRRF.calcular(salarioBruto, valorINSS, dependentes);

        BigDecimal baseCalculo = new BigDecimal("2312.50");
        BigDecimal aliquota = new BigDecimal("0.075");
        BigDecimal parcelaADeduzir = new BigDecimal("158.40");
        BigDecimal esperado = baseCalculo.multiply(aliquota).subtract(parcelaADeduzir);
        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }

    @Test
    void deveCalcularIRRFCorretamenteParaFaixa3() {

        BigDecimal salarioBruto = new BigDecimal("3500.00");
        BigDecimal valorINSS = new BigDecimal("262.50");


        BigDecimal resultado = calculoIRRF.calcular(salarioBruto, valorINSS, dependentes);

        BigDecimal baseCalculo = new BigDecimal("3237.50");
        BigDecimal aliquota = new BigDecimal("0.15");
        BigDecimal parcelaADeduzir = new BigDecimal("370.40");
        BigDecimal esperado = baseCalculo.multiply(aliquota).subtract(parcelaADeduzir);
        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }

    @Test
    void deveCalcularIRRFCorretamenteParaFaixa4() {

        BigDecimal salarioBruto = new BigDecimal("4500.00");
        BigDecimal valorINSS = new BigDecimal("337.50");


        BigDecimal resultado = calculoIRRF.calcular(salarioBruto, valorINSS, dependentes);

        BigDecimal baseCalculo = new BigDecimal("4162.50");
        BigDecimal aliquota = new BigDecimal("0.225");
        BigDecimal parcelaADeduzir = new BigDecimal("651.73");
        BigDecimal esperado = baseCalculo.multiply(aliquota).subtract(parcelaADeduzir);
        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }

    @Test
    void deveCalcularIRRFCorretamenteParaFaixa5() {

        BigDecimal salarioBruto = new BigDecimal("6000.00");
        BigDecimal valorINSS = new BigDecimal("450.00");


        BigDecimal resultado = calculoIRRF.calcular(salarioBruto, valorINSS, dependentes);

        BigDecimal baseCalculo = new BigDecimal("5550.00");
        BigDecimal aliquota = new BigDecimal("0.275");
        BigDecimal parcelaADeduzir = new BigDecimal("884.96");
        BigDecimal esperado = baseCalculo.multiply(aliquota).subtract(parcelaADeduzir);
        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }

    @Test
    void deveRetornarZeroSeCalculoForNegativo() {

        BigDecimal salarioBruto = new BigDecimal("2000.00");
        BigDecimal valorINSS = new BigDecimal("250.00");


        BigDecimal resultado = calculoIRRF.calcular(salarioBruto, valorINSS, dependentes);

        assertEquals(BigDecimal.ZERO.setScale(2), resultado);
    }
}