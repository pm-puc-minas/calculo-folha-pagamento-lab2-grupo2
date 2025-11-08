package com.rh.folhaPagamento.service;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.calculation.*;
import org.springframework.stereotype.Service;

// import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class folhaPagamentoService {

    // --- SERVIÇOS DE CÁLCULO ---
    private final CalculoInsalubridade calculoInsalubridade;
    private final CalculoPericulosidade calculoPericulosidade;
    private final CalculoValeAlimentacao calculoVA;
    private final CalculoValeTransporte calculoVT;
    private final CalculoINSS calculoINSS;
    private final CalculoIRRF calculoIRRF;

    // private final ArquivoService arquivoService;

    // public folhaPagamentoService(
            CalculoInsalubridade calculoInsalubridade,
            CalculoPericulosidade calculoPericulosidade,
            CalculoValeAlimentacao calculoVA,
            CalculoValeTransporte calculoVT,
            CalculoINSS calculoINSS,
            CalculoIRRF calculoIRRF,
            ArquivoService arquivoService) { // <-- Injeção do novo serviço
        this.calculoInsalubridade = calculoInsalubridade;
        this.calculoPericulosidade = calculoPericulosidade;
        this.calculoVA = calculoVA;
        this.calculoVT = calculoVT;
        this.calculoINSS = calculoINSS;
        this.calculoIRRF = calculoIRRF;
        this.arquivoService = arquivoService; // <-- Atribuição
    }

    // public static class DetalheCalculo implements Serializable { // <-- "implements Serializable" da SUA branch
        private static final long serialVersionUID = 1L; // <-- "serialVersionUID" da SUA branch

        // Campos base
        public BigDecimal salarioBase;
        public BigDecimal salarioBruto;
        public BigDecimal totalAdicionais;
        public BigDecimal totalBeneficios;
        public BigDecimal totalDescontos;
        public BigDecimal salarioLiquido;
        public BigDecimal totalAPagar;
        public BigDecimal descontoINSS;
        public BigDecimal descontoIRRF;

        // public BigDecimal insalubridade;
        public BigDecimal periculosidade;
        public BigDecimal valeAlimentacao;
        public BigDecimal valeTransporte;

        // Construtores das duas branches (são compatíveis)
        public DetalheCalculo() {}

        public DetalheCalculo(BigDecimal salarioBruto, BigDecimal totalAdicionais,
                              BigDecimal totalBeneficios, BigDecimal totalDescontos,
                              BigDecimal salarioLiquido) {
            this.salarioBruto = salarioBruto;
            this.totalAdicionais = totalAdicionais;
            this.totalBeneficios = totalBeneficios;
            this.totalDescontos = totalDescontos;
            this.salarioLiquido = salarioLiquido;
        }
    }

    // // public DetalheCalculo calcularFolha(Funcionario funcionario, int diasUteis, int mes, int ano) {

        // === LÓGICA DE CACHE (DA SUA BRANCH) ===

        // String nomeArquivo = "folha_func_" + funcionario.getId() + "_ano_" + ano + "_mes_" + mes + ".dat";
        File arquivoCache = new File(nomeArquivo);

        if (arquivoCache.exists()) {
            System.out.println("CACHE HIT: Lendo arquivo existente: " + nomeArquivo);
            Object objCache = arquivoService.desserializar(nomeArquivo);

            if (objCache instanceof DetalheCalculo) {
                return (DetalheCalculo) objCache;
            }
        }

        // === CACHE MISS: CÁLCULO COMPLETO (LÓGICA DA BRANCH MAIN) ===
        System.out.println("CACHE MISS: Calculando folha para func: " + funcionario.getId() + " (Mês/Ano: " + mes + "/" + ano + ")");

        // BigDecimal salarioBase = funcionario.getSalarioBase();

        // === ADICIONAIS ===
        List<BigDecimal> adicionais = new ArrayList<>();
        BigDecimal valorInsalubridade = BigDecimal.ZERO;
        BigDecimal valorPericulosidade = BigDecimal.ZERO;

        if (funcionario.isAptoPericulosidade()) {
            valorPericulosidade = calculoPericulosidade.calcular(funcionario);
            adicionais.add(valorPericulosidade);
        }
        if (funcionario.getGrauInsalubridade() > 0) {
            valorInsalubridade = calculoInsalubridade.calcular(funcionario);
            adicionais.add(valorInsalubridade);
        }

        BigDecimal totalAdicionais = adicionais.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal salarioBruto = salarioBase.add(totalAdicionais);
        funcionario.setSalarioBruto(salarioBruto);

        // === DESCONTOS ===
        BigDecimal descontoINSS = calculoINSS.calcular(funcionario, diasUteis);
        BigDecimal descontoIRRF = calculoIRRF.calcular(funcionario, diasUteis);
        BigDecimal valorValeTransporte = funcionario.isValeTransporte()
                ? calculoVT.calcular(funcionario, diasUteis)
                : BigDecimal.ZERO;

        List<BigDecimal> descontos = List.of(descontoINSS, descontoIRRF, valorValeTransporte);
        BigDecimal totalDescontos = descontos.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        funcionario.setDescontoINSS(descontoINSS);

        // === BENEFÍCIOS ===
        BigDecimal valorValeAlimentacao = funcionario.isValeAlimentacao()
                ? calculoVA.calcular(funcionario, diasUteis)
                : BigDecimal.ZERO;

        List<BigDecimal> beneficios = List.of(valorValeAlimentacao);
        BigDecimal totalBeneficios = beneficios.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // === CÁLCULOS FINAIS ===
        BigDecimal salarioLiquido = salarioBruto.subtract(totalDescontos);
        BigDecimal totalAPagar = salarioLiquido.add(totalBeneficios);

        // === RETORNO (Lógica da MAIN para preencher TODOS os campos) ===
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
        // Preenchendo os campos novos da MAIN
        r.insalubridade = valorInsalubridade.setScale(2, RoundingMode.HALF_UP);
        r.periculosidade = valorPericulosidade.setScale(2, RoundingMode.HALF_UP);
        r.valeAlimentacao = valorValeAlimentacao.setScale(2, RoundingMode.HALF_UP);
        r.valeTransporte = valorValeTransporte.setScale(2, RoundingMode.HALF_UP);

        // System.out.println("Serializando novo cálculo em: " + nomeArquivo);
        arquivoService.serializar(r, nomeArquivo);

        return r;
    }
}