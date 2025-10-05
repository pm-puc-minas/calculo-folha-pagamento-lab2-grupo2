package com.rh.folhaPagamento.service.calculation;

import com.rh.folhaPagamento.model.Funcionario;

import java.math.BigDecimal;

public interface IBeneficio {

    BigDecimal calcular(Funcionario funcionario, int diasUteis);
}
