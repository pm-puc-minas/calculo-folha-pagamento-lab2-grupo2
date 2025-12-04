package com.rh.folhaPagamento.strategy;

import com.rh.folhaPagamento.model.Funcionario;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CalculoINSS implements CalculoDescontoStrategy {

    @Override
    public BigDecimal calcular(Funcionario funcionario, int diasUteis) {
        BigDecimal salarioBase = funcionario.getSalarioBase();
        BigDecimal desconto;

        BigDecimal faixa1 = new BigDecimal("1302.00");
        BigDecimal faixa2 = new BigDecimal("2571.29");
        BigDecimal faixa3 = new BigDecimal("3856.94");
        BigDecimal faixa4 = new BigDecimal("7507.49");

        if (salarioBase.compareTo(faixa1) <= 0) {
            BigDecimal aliquota = new BigDecimal("0.075");
            desconto = salarioBase.multiply(aliquota);
        } else if (salarioBase.compareTo(faixa2) <= 0) {
            BigDecimal aliquota = new BigDecimal("0.09");
            BigDecimal parcelaADeduzir = new BigDecimal("19.53");
            desconto = salarioBase.multiply(aliquota).subtract(parcelaADeduzir);
        } else if (salarioBase.compareTo(faixa3) <= 0) {
            BigDecimal aliquota = new BigDecimal("0.12");
            BigDecimal parcelaADeduzir = new BigDecimal("96.67");
            desconto = salarioBase.multiply(aliquota).subtract(parcelaADeduzir);
        } else if (salarioBase.compareTo(faixa4) <= 0) {
            BigDecimal aliquota = new BigDecimal("0.14");
            BigDecimal parcelaADeduzir = new BigDecimal("173.81");
            desconto = salarioBase.multiply(aliquota).subtract(parcelaADeduzir);
        } else {
            desconto = new BigDecimal("877.24");
        }

        return desconto.setScale(2, RoundingMode.HALF_UP);
    }
}