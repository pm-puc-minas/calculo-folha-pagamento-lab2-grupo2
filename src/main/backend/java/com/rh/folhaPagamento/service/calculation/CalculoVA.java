package com.rh.folhaPagamento.service.calculation;

import com.rh.folhaPagamento.model.Funcionario;

public class CalculoVA implements Beneficio {

    private int dias;
    private double vale;

    @Override
    public double calcular(Funcionario funcionario) {

        dias = funcionario.getDiasTrabalhados();
        vale = funcionario.getValeAlimentacao() / 30;

        return dias * vale;
    }
}
