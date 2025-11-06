package com.rh.folhaPagamento.dto;

import java.math.BigDecimal;

public class AjusteSalarialRequestDTO {

    private BigDecimal novoSalario;

    // Getters e Setters
    public BigDecimal getNovoSalario() {
        return novoSalario;
    }

    public void setNovoSalario(BigDecimal novoSalario) {
        this.novoSalario = novoSalario;
    }
}