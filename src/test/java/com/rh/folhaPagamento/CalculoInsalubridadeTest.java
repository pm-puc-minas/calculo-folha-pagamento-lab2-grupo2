package com.rh.folhaPagamento;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.calculation.CalculoInsalubridade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testes Unitários para validar a lógica do Adicional de Insalubridade.
 */
public class CalculoInsalubridadeTest {

    private CalculoInsalubridade calculoInsalubridade;
    // O valor deve refletir o que está hardcoded/constante na classe CalculoInsalubridade
    private final BigDecimal SALARIO_MINIMO = new BigDecimal("1518.00");

    @BeforeEach
    void setUp() {

        calculoInsalubridade = new CalculoInsalubridade();
    }

    private Funcionario criarFuncionarioComGrau(int grau) {
        Funcionario f = new Funcionario();
        f.setGrauInsalubridade(grau);
        return f;
    }


    @Test
    void deveCalcularGrauMinimoDe10PorCento() {
        // Grau 1 (10% do salário mínimo)
        Funcionario f = criarFuncionarioComGrau(1);

        BigDecimal resultado = calculoInsalubridade.calcular(f);

        // O valor esperado é calculado DENTRO do teste (melhor prática)
        BigDecimal esperado = SALARIO_MINIMO.multiply(new BigDecimal("0.10"));
        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }

    @Test
    void deveCalcularGrauMedioDe20PorCento() {
        // Grau 2 (20% do salário mínimo)
        Funcionario f = criarFuncionarioComGrau(2);

        BigDecimal resultado = calculoInsalubridade.calcular(f);

        BigDecimal esperado = SALARIO_MINIMO.multiply(new BigDecimal("0.20"));
        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }

    @Test
    void deveCalcularGrauMaximoDe40PorCento() {
        // Grau 3 (40% do salário mínimo)
        Funcionario f = criarFuncionarioComGrau(3);

        BigDecimal resultado = calculoInsalubridade.calcular(f);

        BigDecimal esperado = SALARIO_MINIMO.multiply(new BigDecimal("0.40"));
        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }

    @Test
    void deveCalcularZeroParaGrauInvalido() {
        // Casos de borda: Grau que não está na lista (Ex: 99)
        Funcionario f = criarFuncionarioComGrau(99);

        BigDecimal resultado = calculoInsalubridade.calcular(f);

        BigDecimal esperado = BigDecimal.ZERO;
        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }

    @Test
    void deveCalcularZeroParaGrauZero() {
        // Casos de borda: Sem exposição (Grau 0)
        Funcionario f = criarFuncionarioComGrau(0);

        BigDecimal resultado = calculoInsalubridade.calcular(f);

        BigDecimal esperado = BigDecimal.ZERO;
        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }
}