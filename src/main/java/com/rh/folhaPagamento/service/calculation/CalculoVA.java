package com.rh.folhaPagamento.service.calculation;

import com.rh.folhaPagamento.model.Funcionario;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculoVA implements IBeneficio {

    @Override
    public BigDecimal calcular(Funcionario funcionario, int diasUteis) {
        BigDecimal valorDiarioVA = funcionario.getValorVA();

        BigDecimal beneficioTotal = valorDiarioVA.multiply(new BigDecimal(diasUteis));

        return beneficioTotal.setScale(2, RoundingMode.HALF_UP);
    }
}