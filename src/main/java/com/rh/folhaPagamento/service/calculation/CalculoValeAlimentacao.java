package com.rh.folhaPagamento.service.calculation;

import com.rh.folhaPagamento.model.Funcionario;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
@Service
public class CalculoValeAlimentacao implements IBeneficio {

    @Override
    public BigDecimal calcular(Funcionario funcionario, int diasUteis) {
        BigDecimal valorDiarioVA = funcionario.getValorVA() != null ? funcionario.getValorVA() : BigDecimal.ZERO;

        BigDecimal beneficioTotal = valorDiarioVA.multiply(new BigDecimal(diasUteis));

        return beneficioTotal.setScale(2, RoundingMode.HALF_UP);
    }
}