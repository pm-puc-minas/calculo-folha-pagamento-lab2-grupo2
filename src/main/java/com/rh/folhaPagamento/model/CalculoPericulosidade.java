package com.rh.folhaPagamento.model;

public class CalculoPericulosidade implements Adicional{

    @Override
    public double calcular(Funcionario funcionario) {
        double salarioBase = funcionario.getSalarioBase();

        return salarioBase * 0.30;
    }
}
