package com.rh.folhaPagamento.model;
import java.math.BigDecimal;
import java.util.List;

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
}