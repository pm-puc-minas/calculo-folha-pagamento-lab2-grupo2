package com.rh.folhaPagamento.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class FuncionarioRequestDTO {
    private String nome;
    private String cpf;
    private String cargo;
    private int dependentes;
    private BigDecimal salarioBase;
    private boolean aptoPericulosidade;
    private int grauInsalubridade;
    private boolean valeTransporte;
    private boolean valeAlimentacao;
    private BigDecimal valorVT;
    private BigDecimal valorVA;


    private String login;
    private String senha;
    private int permissao;
}
