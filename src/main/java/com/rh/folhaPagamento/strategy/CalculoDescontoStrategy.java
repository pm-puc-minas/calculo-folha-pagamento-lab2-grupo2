package com.rh.folhaPagamento.strategy;
import com.rh.folhaPagamento.model.Funcionario;
import java.math.BigDecimal;

public interface CalculoDescontoStrategy {
    // Contrato para todos os descontos
    BigDecimal calcular(Funcionario funcionario, int diasUteis);
}