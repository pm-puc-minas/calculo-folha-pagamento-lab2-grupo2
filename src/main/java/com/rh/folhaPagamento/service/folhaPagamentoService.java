package com.rh.folhaPagamento.service;

import com.rh.folhaPagamento.model.Funcionario;
// Importações de Adicionais e VA (que permanecem no pacote 'calculation')
import com.rh.folhaPagamento.service.calculation.CalculoInsalubridade;
import com.rh.folhaPagamento.service.calculation.CalculoPericulosidade;
import com.rh.folhaPagamento.service.calculation.CalculoValeAlimentacao;
// ⭐️ IMPORTAÇÕES DAS NOVAS CLASSES STRATEGY (Seu pacote 'strategy')
import com.rh.folhaPagamento.strategy.CalculoINSS;
import com.rh.folhaPagamento.strategy.CalculoIRRF;
import com.rh.folhaPagamento.strategy.CalculoVT;

import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class folhaPagamentoService {

    // Adicionais e VA mantidos
    private final CalculoInsalubridade calculoInsalubridade;
    private final CalculoPericulosidade calculoPericulosidade;
    private final CalculoValeAlimentacao calculoVA;

    // ⭐️ INJEÇÃO DOS NOVOS OBJETOS STRATEGY
    private final CalculoVT calculoVTStrategy;
    private final CalculoINSS calculoINSSStrategy;
    private final CalculoIRRF calculoIRRFStrategy;


    public folhaPagamentoService(
            CalculoInsalubridade calculoInsalubridade,
            CalculoPericulosidade calculoPericulosidade,
            CalculoValeAlimentacao calculoVA,
            // ⭐️ O CONSTRUTOR AGORA INJETA SUAS NOVAS CLASSES STRATEGY
            CalculoVT calculoVTStrategy,
            CalculoINSS calculoINSSStrategy,
            CalculoIRRF calculoIRRFStrategy) {
        this.calculoInsalubridade = calculoInsalubridade;
        this.calculoPericulosidade = calculoPericulosidade;
        this.calculoVA = calculoVA;
        // ⭐️ ATRIBUIÇÃO DOS NOVOS OBJETOS STRATEGY
        this.calculoVTStrategy = calculoVTStrategy;
        this.calculoINSSStrategy = calculoINSSStrategy;
        this.calculoIRRFStrategy = calculoIRRFStrategy;
    }

    public static class DetalheCalculo implements Serializable {
        private static final long serialVersionUID = 1L;

        public BigDecimal salarioBase;
        public BigDecimal salarioBruto;
        public BigDecimal totalAdicionais;
        public BigDecimal totalBeneficios;
        public BigDecimal totalDescontos;
        public BigDecimal salarioLiquido;
        public BigDecimal totalAPagar;
        public BigDecimal descontoINSS;
        public BigDecimal descontoIRRF;
        public BigDecimal insalubridade;
        public BigDecimal periculosidade;
        public BigDecimal valeAlimentacao;
        public BigDecimal valeTransporte;

        public DetalheCalculo() {}

        public DetalheCalculo(BigDecimal salarioBruto, BigDecimal totalAdicionais,
                              BigDecimal totalBeneficios, BigDecimal totalDescontos,
                              BigDecimal salarioLiquido) {
            this.salarioBase = salarioBruto;
            this.salarioBruto = salarioBruto;
            this.totalAdicionais = totalAdicionais;
            this.totalBeneficios = totalBeneficios;
            this.totalDescontos = totalDescontos;
            this.salarioLiquido = salarioLiquido;
        }
    }

    public DetalheCalculo calcularFolha(Funcionario funcionario, int diasUteis, int mes, int ano) {
        BigDecimal salarioBase = funcionario.getSalarioBase();

        List<BigDecimal> adicionais = new ArrayList<>();
        BigDecimal valorInsalubridade = BigDecimal.ZERO;
        BigDecimal valorPericulosidade = BigDecimal.ZERO;

        // CÁLCULO DE ADICIONAIS (Mantido)
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

        // ⭐️ CÁLCULO DE DESCONTOS (USANDO STRATEGIES)

        // 1. Calcula INSS (usa a Strategy)
        BigDecimal descontoINSS = calculoINSSStrategy.calcular(funcionario, diasUteis);
        // Salva o INSS no objeto para que o IRRF possa usá-lo
        funcionario.setDescontoINSS(descontoINSS);

        // 2. Calcula IRRF (usa a Strategy)
        BigDecimal descontoIRRF = calculoIRRFStrategy.calcular(funcionario, diasUteis);

        // 3. Calcula VT (usa a Strategy)
        // ⚠️ CORREÇÃO DA LÓGICA DE CHAMADA:
        // Se o funcionário usa VT, chama a Strategy. Caso contrário, é ZERO.
        BigDecimal valorValeTransporte = funcionario.isValeTransporte()
                ? calculoVTStrategy.calcular(funcionario, diasUteis)
                : BigDecimal.ZERO;

        List<BigDecimal> descontos = List.of(descontoINSS, descontoIRRF, valorValeTransporte);
        BigDecimal totalDescontos = descontos.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // CÁLCULO DE BENEFÍCIOS E LIQUIDO (Mantido)

        BigDecimal valorValeAlimentacao = funcionario.isValeAlimentacao()
                ? calculoVA.calcular(funcionario, diasUteis)
                : BigDecimal.ZERO;

        List<BigDecimal> beneficios = List.of(valorValeAlimentacao);
        BigDecimal totalBeneficios = beneficios.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal salarioLiquido = salarioBruto.subtract(totalDescontos);
        BigDecimal totalAPagar = salarioLiquido.add(totalBeneficios);

        // PREENCHIMENTO DO DETALHECALCULO (Mantido)
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
        r.insalubridade = valorInsalubridade.setScale(2, RoundingMode.HALF_UP);
        r.periculosidade = valorPericulosidade.setScale(2, RoundingMode.HALF_UP);
        r.valeAlimentacao = valorValeAlimentacao.setScale(2, RoundingMode.HALF_UP);
        r.valeTransporte = valorValeTransporte.setScale(2, RoundingMode.HALF_UP);


        return r;
    }
}