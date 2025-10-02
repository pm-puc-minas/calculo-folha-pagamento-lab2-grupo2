package com.rh.folhaPagamento.service.calculation;

import com.rh.folhaPagamento.model.Funcionario;

public class CalculoInsalubridade implements Adicional {

    private double salarioMinimo =  1518.00;

    @Override
    public double calcular(Funcionario funcionario) {

        double grauInsalubridade = funcionario.getGrauInsalubridade();

        return salarioMinimo * (grauInsalubridade/100);
    }
}
