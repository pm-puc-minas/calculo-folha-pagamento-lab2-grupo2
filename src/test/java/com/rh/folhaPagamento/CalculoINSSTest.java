package com.rh.folhaPagamento;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.calculation.CalculoINSS;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CalculoINSSTest {


    private final CalculoINSS calculoINSS = new CalculoINSS();


    private Funcionario criarFuncionarioComSalario(String salario) {
        Funcionario f = new Funcionario();

        f.setSalarioBase(new BigDecimal(salario));
        return f;
    }

  //O INSS tem alíquotas de 7.5%, 9%, 12%, etc.

    @Test
    void deveCalcularAliquotaMinimaParaSalarioBaixo() {

        Funcionario f = criarFuncionarioComSalario("1000.00");
        BigDecimal valorEsperado = new BigDecimal("75.00");

        BigDecimal descontoCalculado = calculoINSS.calcular(f, 22);

        assertEquals(valorEsperado.setScale(2, RoundingMode.HALF_UP), descontoCalculado);
    }

    @Test
    void deveCalcularAliquotaIntermediariaComParcelaADeduzir() {

        Funcionario f = criarFuncionarioComSalario("2000.00");
        BigDecimal valorEsperado = new BigDecimal("160.47");

        BigDecimal descontoCalculado = calculoINSS.calcular(f, 22);

        assertEquals(valorEsperado.setScale(2, RoundingMode.HALF_UP), descontoCalculado);
    }

    @Test
    void deveAplicarTetoMaximoParaSalarioAlto() {

        Funcionario f = criarFuncionarioComSalario("15000.00");
        BigDecimal valorEsperado = new BigDecimal("877.24");

        BigDecimal descontoCalculado = calculoINSS.calcular(f, 22);

        assertEquals(valorEsperado.setScale(2, RoundingMode.HALF_UP), descontoCalculado);
    }
}