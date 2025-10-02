package com.rh.folhaPagamento.service.calculation;

import com.rh.folhaPagamento.model.Funcionario;

public class CalculoVT implements Descontos {

    @Override
    public double calcular(Funcionario funcionario) {

        return funcionario.getSalarioBase() * 0.06;
    }
}
