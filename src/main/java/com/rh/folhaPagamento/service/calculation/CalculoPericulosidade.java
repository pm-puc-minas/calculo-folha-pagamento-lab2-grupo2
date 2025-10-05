package com.rh.folhaPagamento.service.calculation;

import com.rh.folhaPagamento.model.Funcionario;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculoPericulosidade implements IAdicional {

    @Override
    public BigDecimal calcular(Funcionario funcionario) {
        BigDecimal salarioBase = funcionario.getSalarioBase();
        BigDecimal percentual = new BigDecimal("0.30");

        BigDecimal adicional = salarioBase.multiply(percentual);

        return adicional.setScale(2, RoundingMode.HALF_UP);
    }
}