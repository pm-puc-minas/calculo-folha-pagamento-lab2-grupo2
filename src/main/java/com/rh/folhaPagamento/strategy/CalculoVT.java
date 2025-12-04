package com.rh.folhaPagamento.strategy;
import com.rh.folhaPagamento.model.Funcionario;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CalculoVT implements CalculoDescontoStrategy {

    @Override
    public BigDecimal calcular(Funcionario funcionario, int diasUteis) {
        BigDecimal valorDiarioVT = funcionario.getValorVT() != null ? funcionario.getValorVT() : BigDecimal.ZERO;
        BigDecimal salarioBase = funcionario.getSalarioBase() != null ? funcionario.getSalarioBase() : BigDecimal.ZERO;

        BigDecimal valorTotalBeneficio = valorDiarioVT.multiply(new BigDecimal(diasUteis));
        BigDecimal limiteDesconto = salarioBase.multiply(new BigDecimal("0.06"));

        BigDecimal desconto = limiteDesconto.min(valorTotalBeneficio);

        return desconto.setScale(2, RoundingMode.HALF_UP);
    }
}