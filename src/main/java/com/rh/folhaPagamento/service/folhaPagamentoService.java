package com.rh.folhaPagamento.service;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.calculation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class folhaPagamentoService {


    @Autowired
    private CalculoInsalubridade calculoInsalubridade;

    @Autowired
    private CalculoPericulosidade calculoPericulosidade;

    @Autowired
    private CalculoVA calculoVA;

    @Autowired
    private CalculoVT calculoVT;


    private final CalculoINSS calculoINSS = new CalculoINSS();
    private final CalculoIRRF calculoIRRF = new CalculoIRRF();


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

        funcionario.setSalarioBruto(salarioBruto);

        BigDecimal descontoINSS = calculoINSS.calcular(funcionario, diasUteis);
        totalDescontos = totalDescontos.add(descontoINSS);

        funcionario.setDescontoINSS(totalDescontos);


        BigDecimal descontoIRRF = calculoIRRF.calcular(
                funcionario,
                diasUteis
        );
        totalDescontos = totalDescontos.add(descontoIRRF);



        if (funcionario.isValeTransporte()) {
            totalDescontos = totalDescontos.add(calculoVT.calcular(funcionario, diasUteis));
        }


        if (funcionario.isValeAlimentacao()) {
            totalBeneficios = totalBeneficios.add(calculoVA.calcular(funcionario, diasUteis));
        }


        //RESULTADO

        BigDecimal salarioLiquido = salarioBruto.subtract(totalDescontos);

        BigDecimal totalAPagar = salarioLiquido.add(totalBeneficios);


        return totalAPagar.setScale(2, RoundingMode.HALF_UP);
    }
}
