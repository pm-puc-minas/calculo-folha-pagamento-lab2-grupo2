package com.rh.folhaPagamento.dto;

import java.math.BigDecimal;

public class CalculoFolhaResponse {
    private BigDecimal salarioBase;
    private BigDecimal salarioBruto;
    private BigDecimal totalAdicionais;
    private BigDecimal totalBeneficios;
    private BigDecimal totalDescontos;
    private BigDecimal salarioLiquido;
    private BigDecimal totalAPagar;
    private BigDecimal descontoINSS;
    private BigDecimal descontoIRRF;

    public BigDecimal getSalarioBase() { return salarioBase; }
    public void setSalarioBase(BigDecimal v) { this.salarioBase = v; }

    public BigDecimal getSalarioBruto() { return salarioBruto; }
    public void setSalarioBruto(BigDecimal v) { this.salarioBruto = v; }

    public BigDecimal getTotalAdicionais() { return totalAdicionais; }
    public void setTotalAdicionais(BigDecimal v) { this.totalAdicionais = v; }

    public BigDecimal getTotalBeneficios() { return totalBeneficios; }
    public void setTotalBeneficios(BigDecimal v) { this.totalBeneficios = v; }

    public BigDecimal getTotalDescontos() { return totalDescontos; }
    public void setTotalDescontos(BigDecimal v) { this.totalDescontos = v; }

    public BigDecimal getSalarioLiquido() { return salarioLiquido; }
    public void setSalarioLiquido(BigDecimal v) { this.salarioLiquido = v; }

    public BigDecimal getTotalAPagar() { return totalAPagar; }
    public void setTotalAPagar(BigDecimal v) { this.totalAPagar = v; }

    public BigDecimal getDescontoINSS() { return descontoINSS; }
    public void setDescontoINSS(BigDecimal v) { this.descontoINSS = v; }

    public BigDecimal getDescontoIRRF() { return descontoIRRF; }
    public void setDescontoIRRF(BigDecimal v) { this.descontoIRRF = v; }
}

