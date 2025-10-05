package com.rh.folhaPagamento.service.calculation;

import com.rh.folhaPagamento.model.Funcionario;

import java.math.BigDecimal;

public interface IAdicional {

    BigDecimal calcular(Funcionario funcionario);
}
