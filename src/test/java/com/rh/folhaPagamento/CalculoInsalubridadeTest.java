
package com.rh.folhaPagamento;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.calculation.CalculoInsalubridade;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Classe de Testes Unitários para validar a lógica do Adicional de Insalubridade,
 * que é calculado com base no Salário Mínimo e o grau de exposição (10%, 20% ou 40%).
 */
public class CalculoInsalubridadeTest {

    private final CalculoInsalubridade calculoInsalubridade = new CalculoInsalubridade();
    private final BigDecimal SALARIO_MINIMO_USADO = new BigDecimal("1518.00"); // Constante usada na classe de cálculo

    private Funcionario criarFuncionarioComGrau(int grau) {
        Funcionario f = new Funcionario();

        f.setGrauInsalubridade(grau);
        return f;
    }


    @Test
    void deveCalcularGrauMinimoDe10PorCento() {
        // Grau 1 (10% do salário mínimo)


        Funcionario f = criarFuncionarioComGrau(1);

        BigDecimal valorEsperado = new BigDecimal("151.80");

        BigDecimal adicionalCalculado = calculoInsalubridade.calcular(f);

        assertEquals(valorEsperado.setScale(2, RoundingMode.HALF_UP), adicionalCalculado);
    }

    @Test
    void deveCalcularGrauMedioDe20PorCento() {
        // Grau 2 (20% do salário mínimo)


        Funcionario f = criarFuncionarioComGrau(2);

        BigDecimal valorEsperado = new BigDecimal("303.60");

        BigDecimal adicionalCalculado = calculoInsalubridade.calcular(f);

        assertEquals(valorEsperado.setScale(2, RoundingMode.HALF_UP), adicionalCalculado);
    }

    @Test
    void deveCalcularGrauMaximoDe40PorCento() {
        // Grau 3 (40% do salário mínimo)


        Funcionario f = criarFuncionarioComGrau(3);

        BigDecimal valorEsperado = new BigDecimal("607.20");

        BigDecimal adicionalCalculado = calculoInsalubridade.calcular(f);

        assertEquals(valorEsperado.setScale(2, RoundingMode.HALF_UP), adicionalCalculado);
    }
}