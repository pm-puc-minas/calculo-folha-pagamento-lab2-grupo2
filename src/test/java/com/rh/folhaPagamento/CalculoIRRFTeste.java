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
    private int diasUteis = 22;

    @BeforeEach
    void setUp() {
        calculoIRRF = new CalculoIRRF();
    }

    @Test
    void deveEstarIsentoParaSalarioBaseNaFaixa1() {
        Funcionario funcionario = new Funcionario();
        funcionario.setSalarioBruto(new BigDecimal("2200.00"));
        funcionario.setDependentes(dependentes);
        // INSS é 0 para o cálculo, então não precisa ser subtraído
        funcionario.setDescontoINSS(new BigDecimal("0.00"));

        BigDecimal resultado = calculoIRRF.calcular(funcionario, diasUteis);

        assertEquals(BigDecimal.ZERO.setScale(2), resultado);
    }

    @Test
    void deveCalcularIRRFCorretamenteParaFaixa2() {
        Funcionario funcionario = new Funcionario();
        funcionario.setSalarioBruto(new BigDecimal("2500.00"));
        funcionario.setDependentes(dependentes);

        funcionario.setDescontoINSS(new BigDecimal("187.50"));

        BigDecimal resultado = calculoIRRF.calcular(funcionario, diasUteis);

        BigDecimal baseCalculo = funcionario.getSalarioBruto()
                .subtract(new BigDecimal("189.59").multiply(new BigDecimal(dependentes)))
                .subtract(funcionario.getDescontoINSS());

        BigDecimal aliquota = new BigDecimal("0.075");
        BigDecimal parcelaADeduzir = new BigDecimal("142.80");
        BigDecimal esperado = baseCalculo.multiply(aliquota).subtract(parcelaADeduzir);

        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }

    @Test
    void deveCalcularIRRFCorretamenteParaFaixa3() {
        Funcionario funcionario = new Funcionario();
        funcionario.setSalarioBruto(new BigDecimal("3500.00"));
        funcionario.setDependentes(dependentes);
        funcionario.setDescontoINSS(new BigDecimal("262.50"));

        BigDecimal resultado = calculoIRRF.calcular(funcionario, diasUteis);

        BigDecimal baseCalculo = funcionario.getSalarioBruto()
                .subtract(new BigDecimal("189.59").multiply(new BigDecimal(dependentes)))
                .subtract(funcionario.getDescontoINSS());

        BigDecimal aliquota = new BigDecimal("0.15");
        BigDecimal parcelaADeduzir = new BigDecimal("354.80");
        BigDecimal esperado = baseCalculo.multiply(aliquota).subtract(parcelaADeduzir);

        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }

    @Test
    void deveCalcularIRRFCorretamenteParaFaixa4() {
        Funcionario funcionario = new Funcionario();
        funcionario.setSalarioBruto(new BigDecimal("4500.00"));
        funcionario.setDependentes(dependentes);
        funcionario.setDescontoINSS(new BigDecimal("337.50"));

        BigDecimal resultado = calculoIRRF.calcular(funcionario, diasUteis);

        BigDecimal baseCalculo = funcionario.getSalarioBruto()
                .subtract(new BigDecimal("189.59").multiply(new BigDecimal(dependentes)))
                .subtract(funcionario.getDescontoINSS());

        BigDecimal aliquota = new BigDecimal("0.225");
        BigDecimal parcelaADeduzir = new BigDecimal("636.13");
        BigDecimal esperado = baseCalculo.multiply(aliquota).subtract(parcelaADeduzir);

        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }

    @Test
    void deveCalcularIRRFCorretamenteParaFaixa5() {
        Funcionario funcionario = new Funcionario();
        funcionario.setSalarioBruto(new BigDecimal("6000.00"));
        funcionario.setDependentes(dependentes);
        funcionario.setDescontoINSS(new BigDecimal("450.00"));

        BigDecimal resultado = calculoIRRF.calcular(funcionario, diasUteis);

        BigDecimal baseCalculo = funcionario.getSalarioBruto()
                .subtract(new BigDecimal("189.59").multiply(new BigDecimal(dependentes)))
                .subtract(funcionario.getDescontoINSS());

        BigDecimal aliquota = new BigDecimal("0.275");
        BigDecimal parcelaADeduzir = new BigDecimal("869.36");
        BigDecimal esperado = baseCalculo.multiply(aliquota).subtract(parcelaADeduzir);

        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }

    @Test
    void deveRetornarZeroSeCalculoForNegativo() {
        Funcionario funcionario = new Funcionario();
        funcionario.setSalarioBruto(new BigDecimal("2000.00"));
        funcionario.setDependentes(dependentes);

        funcionario.setDescontoINSS(new BigDecimal("500.00"));

        BigDecimal resultado = calculoIRRF.calcular(funcionario, diasUteis);

        assertEquals(BigDecimal.ZERO.setScale(2), resultado);
    }
}