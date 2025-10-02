package com.rh.folhaPagamento.model;

public class CalculoVT implements Descontos{

    @Override
    public double calcular(Funcionario funcionario) {
        return funcionario.getSalarioBase() * 0.06;
    }
}
