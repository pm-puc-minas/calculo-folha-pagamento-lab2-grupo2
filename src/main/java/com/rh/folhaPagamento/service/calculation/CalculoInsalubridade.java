package com.rh.folhaPagamento.service.calculation;

import com.rh.folhaPagamento.model.Funcionario;

public class CalculoInsalubridade implements Adicional {

    private double salarioMinimo =  1518.00;

    @Override
    public double calcular(Funcionario funcionario) {

        double grauInsalubridade = funcionario.getGrauInsalubridade();
        double adicional = salarioMinimo * (grauInsalubridade/100);
        double salario = funcionario.getSalarioBase();

        funcionario.setSalarioBruto(salario + adicional);
    }
}
