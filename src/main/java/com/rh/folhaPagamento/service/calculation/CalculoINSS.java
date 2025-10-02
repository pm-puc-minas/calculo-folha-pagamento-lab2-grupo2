package com.rh.folhaPagamento.service.calculation;

import com.rh.folhaPagamento.model.Funcionario;

public class CalculoINSS implements Descontos {

    private double aliquota;
    private double salario;
    private double desconto;

    @Override
    public double calcular(Funcionario funcionario) {

        salario = funcionario.getSalarioBase();

        if(salario <= 1518.00){
            aliquota = 0.075;
            desconto = salario * aliquota
            funcionario.setSalarioBruto(salario - desconto);

        }
        else if(salario > 1518.00 && salario<= 2793.88){
            aliquota = 0.09;
            desconto = (salario * aliquota) - 23.37;
            funcionario.setSalarioBruto(salario - desconto);

        } else if (salario > 2793.88 && salario <= 4190.83) {
            aliquota = 0.12;
            desconto = (salario * aliquota) - 98.37
            funcionario.setSalarioBruto(salario - desconto);

        } else if (salario > 4190.83 && salario <=  8157.41) {
            aliquota = 0.14;
            desconto = (salario * aliquota) - 178.87;
            funcionario.setSalarioBruto(salario - desconto);
            
        }
    }
}
