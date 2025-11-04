package com.rh.folhaPagamento.service;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.calculation.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

@Service
public class folhaPagamentoService {

    private final CalculoInsalubridade calculoInsalubridade;
    private final CalculoPericulosidade calculoPericulosidade;
    private final CalculoValeAlimentacao calculoVA;
    private final CalculoValeTransporte calculoVT;
    private final CalculoINSS calculoINSS;
    private final CalculoIRRF calculoIRRF;

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

    public DetalheCalculo calcularFolha(Funcionario funcionario, int diasUteis) {
        BigDecimal salarioBase = funcionario.getSalarioBase();

        //ADICIONAIS (usando Stream para somar se aplicável)
        BigDecimal totalAdicionais = Stream.of(
                funcionario.isAptoPericulosidade() ? calculoPericulosidade.calcular(funcionario) : BigDecimal.ZERO,
                funcionario.getGrauInsalubridade() > 0 ? calculoInsalubridade.calcular(funcionario) : BigDecimal.ZERO
        ).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal salarioBruto = salarioBase.add(totalAdicionais);
        funcionario.setSalarioBruto(salarioBruto);

        //DESCONTOS (usando Stream)
        BigDecimal descontoINSS = calculoINSS.calcular(funcionario, diasUteis);
        BigDecimal descontoIRRF = calculoIRRF.calcular(funcionario, diasUteis);

        BigDecimal totalDescontos = Stream.of(
                descontoINSS,
                descontoIRRF,
                funcionario.isValeTransporte() ? calculoVT.calcular(funcionario, diasUteis) : BigDecimal.ZERO
        ).reduce(BigDecimal.ZERO, BigDecimal::add);

        funcionario.setDescontoINSS(descontoINSS);

        //BENEFÍCIOS (usando Stream)
        BigDecimal totalBeneficios = Stream.of(
                funcionario.isValeAlimentacao() ? calculoVA.calcular(funcionario, diasUteis) : BigDecimal.ZERO
        ).reduce(BigDecimal.ZERO, BigDecimal::add);

        //CÁLCULOS FINAIS
        BigDecimal salarioLiquido = salarioBruto.subtract(totalDescontos);
        BigDecimal totalAPagar = salarioLiquido.add(totalBeneficios);

        //CRIAÇÃO DO OBJETO DE RETORNO
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
