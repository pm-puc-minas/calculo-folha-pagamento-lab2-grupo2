package com.rh.folhaPagamento.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Funcionario {
    private long id;
    private String nome;
    private String cpf;
    private String cargo;
    private Usuario usuario;
    private int dependentes;
    private double salarioBruto;
    private double salarioLiquido;
    private double salarioBase;
    private boolean aptoPericulosidade;
    private int grauInsalubridade;
    private boolean valeTransporte;
    private boolean valeAlimentacao;
    private double valorVT;
    private double valorVA;
    private int diasTrabalhados;
    private double valorINSS;



}
