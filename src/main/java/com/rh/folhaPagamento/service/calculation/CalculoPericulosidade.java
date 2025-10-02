package com.rh.folhaPagamento.service.calculation;

import com.rh.folhaPagamento.model.Funcionario;

public class CalculoPericulosidade implements Adicional {

    @Override
    public double calcular(Funcionario funcionario) {
        double salarioBase = funcionario.getSalarioBase();

        return salarioBase * 0.30;
    }
}
