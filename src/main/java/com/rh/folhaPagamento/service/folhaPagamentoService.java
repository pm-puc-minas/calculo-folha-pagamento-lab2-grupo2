package com.rh.folhaPagamento.service;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.calculation.*;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class folhaPagamentoService {

    // DECLARAÇÃO:
    private final CalculoInsalubridade calculoInsalubridade;
    private final CalculoPericulosidade calculoPericulosidade;
    private final CalculoValeAlimentacao calculoVA;
    private final CalculoValeTransporte calculoVT;
    private final CalculoINSS calculoINSS;
    private final CalculoIRRF calculoIRRF;

    // CONSTRUTOR: Injeção de todas as dependências
    public folhaPagamentoService(
            CalculoInsalubridade calculoInsalubridade,
            CalculoPericulosidade calculoPericulosidade,
            CalculoValeAlimentacao calculoVA,
            CalculoValeTransporte calculoVT,
            CalculoINSS calculoINSS,
            CalculoIRRF calculoIRRF) {
        this.calculoInsalubridade = calculoInsalubridade;
        this.calculoPericulosidade = calculoPericulosidade;
        this.calculoVA = calculoVA;
        this.calculoVT = calculoVT;
        this.calculoINSS = calculoINSS;
        this.calculoIRRF = calculoIRRF;
    }

    public static class DetalheCalculo {
        public BigDecimal salarioBase;
        public BigDecimal salarioBruto;
        public BigDecimal totalAdicionais;
        public BigDecimal totalBeneficios;
        public BigDecimal totalDescontos;
        public BigDecimal salarioLiquido;
        public BigDecimal totalAPagar;
        public BigDecimal descontoINSS;
        public BigDecimal descontoIRRF;
    }

    public DetalheCalculo calcularFolha(Funcionario funcionario, int diasUteis){
        BigDecimal totalAdicionais = BigDecimal.ZERO;
        BigDecimal totalDescontos = BigDecimal.ZERO;
        BigDecimal totalBeneficios = BigDecimal.ZERO;
        BigDecimal salarioBase = funcionario.getSalarioBase();
        BigDecimal salarioBruto = salarioBase;

        if (funcionario.isAptoPericulosidade()) {
            totalAdicionais = totalAdicionais.add(calculoPericulosidade.calcular(funcionario));
        }
        if (funcionario.getGrauInsalubridade() > 0) {
            totalAdicionais = totalAdicionais.add(calculoInsalubridade.calcular(funcionario));
        }
        salarioBruto = salarioBase.add(totalAdicionais);
        funcionario.setSalarioBruto(salarioBruto);

        BigDecimal descontoINSS = calculoINSS.calcular(funcionario, diasUteis);
        totalDescontos = totalDescontos.add(descontoINSS);
        funcionario.setDescontoINSS(descontoINSS);

        BigDecimal descontoIRRF = calculoIRRF.calcular(funcionario, diasUteis);
        totalDescontos = totalDescontos.add(descontoIRRF);

        if (funcionario.isValeTransporte()) {
            totalDescontos = totalDescontos.add(calculoVT.calcular(funcionario, diasUteis));
        }
        if (funcionario.isValeAlimentacao()) {
            totalBeneficios = totalBeneficios.add(calculoVA.calcular(funcionario, diasUteis));
        }

        BigDecimal salarioLiquido = salarioBruto.subtract(totalDescontos);
        BigDecimal totalAPagar = salarioLiquido.add(totalBeneficios);

        DetalheCalculo r = new DetalheCalculo();
        r.salarioBase = salarioBase.setScale(2, RoundingMode.HALF_UP);
        r.salarioBruto = salarioBruto.setScale(2, RoundingMode.HALF_UP);
        r.totalAdicionais = totalAdicionais.setScale(2, RoundingMode.HALF_UP);
        r.totalBeneficios = totalBeneficios.setScale(2, RoundingMode.HALF_UP);
        r.totalDescontos = totalDescontos.setScale(2, RoundingMode.HALF_UP);
        r.salarioLiquido = salarioLiquido.setScale(2, RoundingMode.HALF_UP);
        r.totalAPagar = totalAPagar.setScale(2, RoundingMode.HALF_UP);
        r.descontoINSS = descontoINSS.setScale(2, RoundingMode.HALF_UP);
        r.descontoIRRF = descontoIRRF.setScale(2, RoundingMode.HALF_UP);
        return r;
    }
}