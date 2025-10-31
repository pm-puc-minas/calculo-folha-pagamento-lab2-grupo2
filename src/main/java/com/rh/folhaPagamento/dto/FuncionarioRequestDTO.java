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
    private Boolean aptoPericulosidade;
    private int grauInsalubridade;
    private Boolean valeTransporte;
    private Boolean valeAlimentacao;
    private BigDecimal valorVT;
    private BigDecimal valorVA;
    private Integer diasUteis;

    private String login;
    private String senha;
    private int permissao;
}
