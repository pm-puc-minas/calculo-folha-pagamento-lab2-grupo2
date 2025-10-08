package com.rh.folhaPagamento;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.calculation.CalculoINSS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculoINSSTeste {

    private CalculoINSS calculoINSS;
    private Funcionario funcionario;


    @BeforeEach
    void setUp() {
        calculoINSS = new CalculoINSS();
        funcionario = new Funcionario();
    }

    @Test
    void deveCalcularINSSCorretamenteParaFaixa1() {

        funcionario.setSalarioBase(new BigDecimal("1000.00"));

        BigDecimal resultado = calculoINSS.calcular(funcionario, 0); // O valor de diasUteis não afeta o resultado

        BigDecimal esperado = new BigDecimal("1000.00").multiply(new BigDecimal("0.075"));
        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }

    @Test
    void deveCalcularINSSCorretamenteParaFaixa2() {

        funcionario.setSalarioBase(new BigDecimal("2000.00"));

        BigDecimal resultado = calculoINSS.calcular(funcionario, 0);

        BigDecimal aliquota = new BigDecimal("0.09");
        BigDecimal parcelaADeduzir = new BigDecimal("19.53");
        BigDecimal esperado = new BigDecimal("2000.00").multiply(aliquota).subtract(parcelaADeduzir);
        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }

    @Test
    void deveCalcularINSSCorretamenteParaFaixa3() {

        funcionario.setSalarioBase(new BigDecimal("3000.00"));

        // Ação (Act)
        BigDecimal resultado = calculoINSS.calcular(funcionario, 0);

        BigDecimal aliquota = new BigDecimal("0.12");
        BigDecimal parcelaADeduzir = new BigDecimal("96.67");
        BigDecimal esperado = new BigDecimal("3000.00").multiply(aliquota).subtract(parcelaADeduzir);
        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }

    @Test
    void deveCalcularINSSCorretamenteParaFaixa4() {

        funcionario.setSalarioBase(new BigDecimal("7000.00"));

        BigDecimal resultado = calculoINSS.calcular(funcionario, 0);


        BigDecimal aliquota = new BigDecimal("0.14");
        BigDecimal parcelaADeduzir = new BigDecimal("173.81");
        BigDecimal esperado = new BigDecimal("7000.00").multiply(aliquota).subtract(parcelaADeduzir);
        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }

    @Test
    void deveCalcularTetoParaSalarioSuperiorAoTeto() {

        funcionario.setSalarioBase(new BigDecimal("10000.00"));

        BigDecimal resultado = calculoINSS.calcular(funcionario, 0);

        BigDecimal esperado = new BigDecimal("877.24");
        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }
}