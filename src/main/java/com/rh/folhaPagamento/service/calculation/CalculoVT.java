package com.rh.folhaPagamento.service.calculation;

import com.rh.folhaPagamento.model.Funcionario;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
@Service
public class CalculoVT implements IDescontos {

    @Override
    public BigDecimal calcular(Funcionario funcionario, int diasUteis) {
        BigDecimal valorDiarioVT = funcionario.getValorVT();
        BigDecimal salarioBase = funcionario.getSalarioBase();

        BigDecimal valorTotalBeneficio = valorDiarioVT.multiply(new BigDecimal(diasUteis));
        BigDecimal limiteDesconto = salarioBase.multiply(new BigDecimal("0.06"));

        BigDecimal desconto = limiteDesconto.min(valorTotalBeneficio);

        return desconto.setScale(2, RoundingMode.HALF_UP);
    }
}