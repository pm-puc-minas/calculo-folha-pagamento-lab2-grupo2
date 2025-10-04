package com.rh.folhaPagamento.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "funcionario")
@Getter
@Setter
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpf;

    private String cargo;
    private int dependentes;

    @Column(name = "salario_base", nullable = false)
    private double salarioBase;
    @Column(name = "apto_periculosidade", nullable = false)
    private boolean aptoPericulosidade;
    private int grauInsalubridade;
    private boolean valeTransporte;
    private boolean valeAlimentacao;

    @Column(name = "valor_vt")
    private double valorVT;

    @Column(name = "valor_va")
    private double valorVA;

    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false, unique = true)
    private Usuario usuario;
}