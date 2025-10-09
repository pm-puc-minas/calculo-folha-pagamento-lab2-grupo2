package com.rh.folhaPagamento;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.calculation.CalculoINSS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testes Unitários para validar o cálculo de Desconto do INSS (Progressivo).
 */
class CalculoINSSTest {

    private CalculoINSS calculoINSS;
    private final int DIAS_UTEIS_IGNORADO = 0; // O cálculo do INSS não usa dias úteis

    @BeforeEach
    void setUp() {
        calculoINSS = new CalculoINSS();
    }

    private Funcionario criarFuncionarioComSalario(String salario) {
        Funcionario f = new Funcionario();
        f.setSalarioBase(new BigDecimal(salario));
        return f;
    }


    @Test
    void deveCalcularINSSCorretamenteParaFaixa1() {
        // Salário: 1000.00 (7.5%)
        Funcionario f = criarFuncionarioComSalario("1000.00");

        BigDecimal resultado = calculoINSS.calcular(f, DIAS_UTEIS_IGNORADO);

        // Fórmula: Salário * 7.5%
        BigDecimal esperado = new BigDecimal("1000.00").multiply(new BigDecimal("0.075"));
        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }

    @Test
    void deveCalcularINSSCorretamenteParaFaixa2() {
        // Salário: 2000.00 (9% com parcela a deduzir)
        Funcionario f = criarFuncionarioComSalario("2000.00");

        BigDecimal resultado = calculoINSS.calcular(f, DIAS_UTEIS_IGNORADO);

        // Fórmula: Salário * 9% - Parcela a Deduzir (19.53)
        BigDecimal aliquota = new BigDecimal("0.09");
        BigDecimal parcelaADeduzir = new BigDecimal("19.53");
        BigDecimal esperado = new BigDecimal("2000.00").multiply(aliquota).subtract(parcelaADeduzir);
        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }

    @Test
    void deveCalcularINSSCorretamenteParaFaixa3() {
        // Salário: 3000.00 (12% com parcela a deduzir)
        Funcionario f = criarFuncionarioComSalario("3000.00");

        BigDecimal resultado = calculoINSS.calcular(f, DIAS_UTEIS_IGNORADO);

        // Fórmula: Salário * 12% - Parcela a Deduzir (96.67)
        BigDecimal aliquota = new BigDecimal("0.12");
        BigDecimal parcelaADeduzir = new BigDecimal("96.67");
        BigDecimal esperado = new BigDecimal("3000.00").multiply(aliquota).subtract(parcelaADeduzir);
        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }

    @Test
    void deveCalcularINSSCorretamenteParaFaixa4() {
        // Salário: 7000.00 (14% com parcela a deduzir, abaixo do teto)
        Funcionario f = criarFuncionarioComSalario("7000.00");

        BigDecimal resultado = calculoINSS.calcular(f, DIAS_UTEIS_IGNORADO);

        // Fórmula: Salário * 14% - Parcela a Deduzir (173.81)
        BigDecimal aliquota = new BigDecimal("0.14");
        BigDecimal parcelaADeduzir = new BigDecimal("173.81");
        BigDecimal esperado = new BigDecimal("7000.00").multiply(aliquota).subtract(parcelaADeduzir);
        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }

    @Test
    void deveCalcularTetoParaSalarioSuperiorAoTeto() {
        // Salário: 15000.00 (Acima do teto de 7507.49)
        Funcionario f = criarFuncionarioComSalario("15000.00");

        BigDecimal resultado = calculoINSS.calcular(f, DIAS_UTEIS_IGNORADO);

        // O valor esperado é o teto (877.24)
        BigDecimal esperado = new BigDecimal("877.24");
        assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), resultado);
    }
}