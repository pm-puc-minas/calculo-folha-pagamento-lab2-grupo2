package com.rh.folhaPagamento;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.calculation.CalculoPericulosidade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculoPericulosidadeTeste {

    private CalculoPericulosidade calculoPericulosidade;

    @BeforeEach
    void setUp() {
        calculoPericulosidade = new CalculoPericulosidade();
    }

    @Test
    void deveCalcularPericulosidadeCorretamenteQuandoApto() {

        Funcionario funcionario = new Funcionario();
        funcionario.setSalarioBase(new BigDecimal("1000.00"));
        funcionario.setAptoPericulosidade(true);

        BigDecimal percentual = new BigDecimal("0.30");

        BigDecimal resultado = calculoPericulosidade.calcular(funcionario);
        BigDecimal esperado = new BigDecimal("1000.00").multiply(percentual);
        assertEquals(esperado.setScale(2), resultado);
    }

    @Test
    void deveRetornarZeroQuandoNaoApto() {

        Funcionario funcionario = new Funcionario();
        funcionario.setSalarioBase(new BigDecimal("2000.00"));
        funcionario.setAptoPericulosidade(false);

        BigDecimal resultado = calculoPericulosidade.calcular(funcionario);

        BigDecimal esperado = BigDecimal.ZERO;

        assertEquals(esperado.setScale(2), resultado);
    }
}
