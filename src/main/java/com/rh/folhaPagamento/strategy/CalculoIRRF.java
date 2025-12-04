package com.rh.folhaPagamento.strategy;

import com.rh.folhaPagamento.model.Funcionario;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CalculoIRRF implements CalculoDescontoStrategy {

    @Override
    public BigDecimal calcular(Funcionario funcionario, int diasUteis) {
        BigDecimal salarioBruto = funcionario.getSalarioBruto() != null ? funcionario.getSalarioBruto() : BigDecimal.ZERO;
        // O IRRF depende do INSS, que foi salvo no objeto Funcionario
        BigDecimal descontoINSS = funcionario.getDescontoINSS() != null ? funcionario.getDescontoINSS() : BigDecimal.ZERO;
        int numeroDependentes = funcionario.getDependentes();

        return calcularIRRFInterno(salarioBruto, descontoINSS, numeroDependentes);
    }

    public BigDecimal calcularIRRFInterno(BigDecimal salarioBruto, BigDecimal descontoINSS, int numeroDependentes) {

        BigDecimal deducaoPorDependente = new BigDecimal("189.59");
        BigDecimal totalDeducaoDependentes = deducaoPorDependente.multiply(new BigDecimal(numeroDependentes));

        BigDecimal baseCalculo = salarioBruto.subtract(descontoINSS).subtract(totalDeducaoDependentes);

        BigDecimal imposto;

        if (baseCalculo.compareTo(new BigDecimal("1903.98")) <= 0) {
            imposto = BigDecimal.ZERO;
        } else if (baseCalculo.compareTo(new BigDecimal("2826.65")) <= 0) {
            BigDecimal aliquota = new BigDecimal("0.075");
            BigDecimal parcelaADeduzir = new BigDecimal("142.80");
            imposto = baseCalculo.multiply(aliquota).subtract(parcelaADeduzir);
        } else if (baseCalculo.compareTo(new BigDecimal("3751.05")) <= 0) {
            BigDecimal aliquota = new BigDecimal("0.15");
            BigDecimal parcelaADeduzir = new BigDecimal("354.80");
            imposto = baseCalculo.multiply(aliquota).subtract(parcelaADeduzir);
        } else if (baseCalculo.compareTo(new BigDecimal("4664.68")) <= 0) {
            BigDecimal aliquota = new BigDecimal("0.225");
            BigDecimal parcelaADeduzir = new BigDecimal("636.13");
            imposto = baseCalculo.multiply(aliquota).subtract(parcelaADeduzir);
        } else {
            BigDecimal aliquota = new BigDecimal("0.275");
            BigDecimal parcelaADeduzir = new BigDecimal("869.36");
            imposto = baseCalculo.multiply(aliquota).subtract(parcelaADeduzir);
        }

        if (imposto.compareTo(BigDecimal.ZERO) < 0) {
            imposto = BigDecimal.ZERO;
        }

        return imposto.setScale(2, RoundingMode.HALF_UP);
    }
}