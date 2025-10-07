
package com.rh.folhaPagamento;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.calculation.CalculoPericulosidade;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

//30% do salário base

public class CalculoPericulosidadeTest {


    private final CalculoPericulosidade calculoPericulosidade = new CalculoPericulosidade();


    private Funcionario criarFuncionarioComSalario(String salario) {
        Funcionario f = new Funcionario();
        f.setSalarioBase(new BigDecimal(salario));
        return f;
    }


    @Test
    void deveCalcularAdicionalParaSalarioBaixo() {
        // Teste simples para garantir que 30% seja calculado


        Funcionario f = criarFuncionarioComSalario("1000.00");
        BigDecimal valorEsperado = new BigDecimal("300.00");

        BigDecimal adicionalCalculado = calculoPericulosidade.calcular(f);

        assertEquals(valorEsperado.setScale(2, RoundingMode.HALF_UP), adicionalCalculado);
    }

    @Test
    void deveCalcularAdicionalParaSalarioAltoComArredondamento() {
        // Teste com um valor que exige arredondamento (para garantir precisão do BigDecimal)


        Funcionario f = criarFuncionarioComSalario("4150.55");

        BigDecimal valorEsperado = new BigDecimal("1245.17");

        BigDecimal adicionalCalculado = calculoPericulosidade.calcular(f);

        assertEquals(valorEsperado.setScale(2, RoundingMode.HALF_UP), adicionalCalculado);
    }
}