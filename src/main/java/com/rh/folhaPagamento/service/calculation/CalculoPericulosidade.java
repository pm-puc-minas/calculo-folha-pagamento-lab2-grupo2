package com.rh.folhaPagamento.service.calculation;

import com.rh.folhaPagamento.model.Funcionario;

public class CalculoPericulosidade implements Adicional {

    @Override
    public void calcular(Funcionario funcionario) {
        double salarioBase = funcionario.getSalarioBase();
        double adicional = salarioBase*0.30 ;
        funcionario.setSalarioBruto(salarioBase + adicional);
    }
}
