package com.rh.folhaPagamento.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FuncionarioRequestDTO {
    private String nome;
    private String cpf;
    private String cargo;
    private int dependentes;
    private double salarioBase;
    private boolean aptoPericulosidade;
    private int grauInsalubridade;
    private boolean valeTransporte;
    private boolean valeAlimentacao;
    private double valorVT;
    private double valorVA;


    private String login;
    private String senha;
    private int permissao;
}
