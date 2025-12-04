package com.rh.folhaPagamento.strategy;


import com.rh.folhaPagamento.model.Funcionario;

public interface CalculoDescontoStrategy {

    /**
     * Define o contrato para calcular um tipo específico de desconto.
     * @param funcionario O objeto com todos os dados salariais.
     * @return O valor do desconto (em double).
     */
    double calcular(Funcionario funcionario);
}