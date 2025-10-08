package com.rh.folhaPagamento.model;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter

public class FolhaDePagamento {
    private List<Funcionario> funcionarios;
    private BigDecimal totalBeneficios;
    private BigDecimal totalAdicionais;
    private BigDecimal totalDescontos;
    private BigDecimal salarioBruto;
    private BigDecimal salarioLiquido;
    private int mesReferencia;
    private int anoReferencia;
    private int id;

    public FolhaDePagamento() {}

    public FolhaDePagamento(int id, int mesReferencia, int anoReferencia) {
        this.id = id;
        this.mesReferencia = mesReferencia;
        this.anoReferencia = anoReferencia;
    }
}