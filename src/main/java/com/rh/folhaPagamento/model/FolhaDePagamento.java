package com.rh.folhaPagamento.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable; // <-- ADICIONADO
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "folha_pagamento")
public class FolhaDePagamento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "mes_referencia", nullable = false)
    private Integer mesReferencia;

    @Column(name = "ano_referencia", nullable = false)
    private Integer anoReferencia;

    @Column(name = "salario_bruto", nullable = false)
    private BigDecimal salarioBruto;

    @Column(name = "total_adicionais", nullable = false)
    private BigDecimal totalAdicionais;

    @Column(name = "total_beneficios", nullable = false)
    private BigDecimal totalBeneficios;

    @Column(name = "total_descontos", nullable = false)
    private BigDecimal totalDescontos;

    @Column(name = "salario_liquido", nullable = false)
    private BigDecimal salarioLiquido;

    @Column(name = "valor_insalubridade")
    private BigDecimal insalubridade;

    @Column(name = "valor_periculosidade")
    private BigDecimal periculosidade;

    @Column(name = "valor_inss")
    private BigDecimal inss;

    @Column(name = "valor_irrf")
    private BigDecimal irrf;

    @Column(name = "valor_vale_alimentacao")
    private BigDecimal valeAlimentacao;

    @Column(name = "valor_vale_transporte")
    private BigDecimal valeTransporte;

    @ManyToOne
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;
}
