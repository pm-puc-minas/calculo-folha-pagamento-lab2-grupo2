package com.rh.folhaPagamento.service;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.calculation.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
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
        public DetalheCalculo() {
            // construtor padrão
        }
        public BigDecimal salarioBase;
        public BigDecimal salarioBruto;
        public BigDecimal totalAdicionais;
        public BigDecimal totalBeneficios;
        public BigDecimal totalDescontos;
        public BigDecimal salarioLiquido;
        public BigDecimal totalAPagar;
        public BigDecimal descontoINSS;
        public BigDecimal descontoIRRF;
        
        // Construtor adicional para facilitar criação em testes
        public DetalheCalculo(BigDecimal salarioBruto, BigDecimal totalAdicionais, BigDecimal totalBeneficios, BigDecimal totalDescontos, BigDecimal salarioLiquido) {
            this.salarioBase = salarioBruto;
            this.salarioBruto = salarioBruto;
            this.totalAdicionais = totalAdicionais;
            this.totalBeneficios = totalBeneficios;
            this.totalDescontos = totalDescontos;
            this.salarioLiquido = salarioLiquido;
        }
    }

    public DetalheCalculo calcularFolha(Funcionario funcionario, int diasUteis) {
        BigDecimal salarioBase = funcionario.getSalarioBase();

        // === ADICIONAIS ===
        List<BigDecimal> adicionais = new ArrayList<>();

        if (funcionario.isAptoPericulosidade()) {
            adicionais.add(calculoPericulosidade.calcular(funcionario));
        }
        if (funcionario.getGrauInsalubridade() > 0) {
            adicionais.add(calculoInsalubridade.calcular(funcionario));
        }

        // Soma usando Stream
        BigDecimal totalAdicionais = adicionais.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal salarioBruto = salarioBase.add(totalAdicionais);
        funcionario.setSalarioBruto(salarioBruto);

        // === DESCONTOS ===
        BigDecimal descontoINSS = calculoINSS.calcular(funcionario, diasUteis);
        funcionario.setDescontoINSS(descontoINSS);
        BigDecimal descontoIRRF = calculoIRRF.calcular(funcionario, diasUteis);

        List<BigDecimal> descontos = new ArrayList<>();
        descontos.add(descontoINSS);
        descontos.add(descontoIRRF);

        if (funcionario.isValeTransporte()) {
            descontos.add(calculoVT.calcular(funcionario, diasUteis));
        }

        BigDecimal totalDescontos = descontos.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // === BENEFÍCIOS ===
        List<BigDecimal> beneficios = new ArrayList<>();

        if (funcionario.isValeAlimentacao()) {
            beneficios.add(calculoVA.calcular(funcionario, diasUteis));
        }

        BigDecimal totalBeneficios = beneficios.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // === CÁLCULOS FINAIS ===
        BigDecimal salarioLiquido = salarioBruto.subtract(totalDescontos);
        BigDecimal totalAPagar = salarioLiquido.add(totalBeneficios);

        // === CRIAÇÃO DO OBJETO DE RETORNO ===
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
