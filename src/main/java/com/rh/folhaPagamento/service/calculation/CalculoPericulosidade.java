package com.rh.folhaPagamento.service.calculation;

import com.rh.folhaPagamento.model.Funcionario;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
@Service
public class CalculoPericulosidade implements IAdicional {

    @Override
    public BigDecimal calcular(Funcionario funcionario) {
        if(funcionario.isAptoPericulosidade()) {
            BigDecimal salarioBase = funcionario.getSalarioBase();
            BigDecimal percentual = new BigDecimal("0.30");

            BigDecimal adicional = salarioBase.multiply(percentual);

            return adicional.setScale(2, RoundingMode.HALF_UP);
        }
        else {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
    }
}