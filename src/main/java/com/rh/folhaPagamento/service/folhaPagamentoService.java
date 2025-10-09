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
    private final CalculoVA calculoVA;
    private final CalculoVT calculoVT;
    private final CalculoINSS calculoINSS;
    private final CalculoIRRF calculoIRRF;

    // CONSTRUTOR: Injeção de todas as dependências
    public folhaPagamentoService(
            CalculoInsalubridade calculoInsalubridade,
            CalculoPericulosidade calculoPericulosidade,
            CalculoVA calculoVA,
            CalculoVT calculoVT,
            CalculoINSS calculoINSS,
            CalculoIRRF calculoIRRF) {
        this.calculoInsalubridade = calculoInsalubridade;
        this.calculoPericulosidade = calculoPericulosidade;
        this.calculoVA = calculoVA;
        this.calculoVT = calculoVT;
        this.calculoINSS = calculoINSS;
        this.calculoIRRF = calculoIRRF;
    }


    public BigDecimal calcularFolha(Funcionario funcionario, int diasUteis) {

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

        // ATUALIZA O SALÁRIO BRUTO NO OBJETO
        funcionario.setSalarioBruto(salarioBruto);

        // 1. CÁLCULO DO INSS
        BigDecimal descontoINSS = calculoINSS.calcular(funcionario, diasUteis);
        totalDescontos = totalDescontos.add(descontoINSS);

        // ATUALIZA O DESCONTO INSS NO OBJETO para ser usado no IRRF
        funcionario.setDescontoINSS(descontoINSS);


        // 2. CÁLCULO DO IRRF
        BigDecimal descontoIRRF = calculoIRRF.calcular(
                funcionario,
                diasUteis
        );
        totalDescontos = totalDescontos.add(descontoIRRF);


        // 3. CÁLCULO DO VALE TRANSPORTE
        if (funcionario.isValeTransporte()) {
            totalDescontos = totalDescontos.add(calculoVT.calcular(funcionario, diasUteis));
        }


        // 4. CÁLCULO DO VALE ALIMENTAÇÃO
        if (funcionario.isValeAlimentacao()) {
            totalBeneficios = totalBeneficios.add(calculoVA.calcular(funcionario, diasUteis));
        }


        // RESULTADO
        BigDecimal salarioLiquido = salarioBruto.subtract(totalDescontos);

        BigDecimal totalAPagar = salarioLiquido.add(totalBeneficios);


        return totalAPagar.setScale(2, RoundingMode.HALF_UP);
    }
}