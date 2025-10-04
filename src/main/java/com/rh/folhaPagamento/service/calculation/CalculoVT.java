package com.rh.folhaPagamento.service.calculation;

import com.rh.folhaPagamento.model.Funcionario;

public class CalculoVT implements Descontos {

    @Override
    public double calcular(Funcionario funcionario) {
        double salario = funcionario.getSalarioBase();
        double desconto = salario * 0.06;
        funcionario.setSalarioBruto(salario - desconto);
    }
}
