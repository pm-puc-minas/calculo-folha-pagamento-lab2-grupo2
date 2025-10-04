package com.rh.folhaPagamento.service.calculation;

import com.rh.folhaPagamento.model.Funcionario;

public class CalculoVA implements Beneficio {

    private int dias;
    private double vale;

    @Override
    public double calcular(Funcionario funcionario) {

        double salario = funcionario.getSalarioBase();
        dias = funcionario.getDiasTrabalhados();
        vale = funcionario.getValorVA() / 30;
        double beneficio = dias * vale ;

        funcionario.setSalarioBruto(salario + beneficio);
    }
}
