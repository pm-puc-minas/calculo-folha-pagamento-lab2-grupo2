package com.rh.folhaPagamento.service.calculation;

import com.rh.folhaPagamento.model.Funcionario;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculoInsalubridade implements IAdicional {

    private final BigDecimal salarioMinimo = new BigDecimal("1518.00");

    @Override
    public BigDecimal calcular(Funcionario funcionario) {
        int grau = funcionario.getGrauInsalubridade();
        BigDecimal percentual;

        switch (grau) {
            case 1:
                percentual = new BigDecimal("0.10");
                break;
            case 2:
                percentual = new BigDecimal("0.20");
                break;
            case 3:
                percentual = new BigDecimal("0.40");
                break;
            default:
                percentual = BigDecimal.ZERO;
                break;
        }

        BigDecimal adicional = salarioMinimo.multiply(percentual);

        return adicional.setScale(2, RoundingMode.HALF_UP);
    }
}