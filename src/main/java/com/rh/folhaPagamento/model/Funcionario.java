package com.rh.folhaPagamento.model;

public class Funcionario {
    private long id;
    private String nome;
    private String cpf;
    private String cargo;
    private Usuario usuario;
    private int dependentes;
    private double salario;
    private double salarioBase;
    private boolean aptoPericulosidade;
    private int grauInsalubridade;
    private boolean valeTransporte;
    private boolean valeAlimentacao;
    private double valorVT;
    private double valorVA;
    private int diasTrabalhados;

    public double getSalarioBase(){
        return salarioBase;
    }
    public int getGrauInsalubridade(){
        return grauInsalubridade;
    }
    public double getValeAlimentacao(){
        return valorVA;
    }
    public double getValeTransporte(){
        return valorVT;
    }
    public double getDiasTrabalhados(){
        return diasTrabalhados;
    }

}
