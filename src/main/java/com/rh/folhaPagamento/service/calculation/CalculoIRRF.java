package com.rh.folhaPagamento.service.calculation;

import com.rh.folhaPagamento.model.Funcionario;

public class CalculoIRRF implements Descontos {

    private double baseCalculo;
    @Override
    public double calcular(Funcionario funcionario) {

        baseCalculo = funcionario.getSalarioBruto();
    }
}
