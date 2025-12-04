

package com.rh.folhaPagamento.strategy;
import org.springframework.stereotype.Component;
import com.rh.folhaPagamento.model.Funcionario;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CalculoVT implements CalculoDescontoStrategy {

    private static final BigDecimal PERCENTUAL_MAXIMO_VT = new BigDecimal("0.06"); // 6%

    @Override
    public double calcular(Funcionario funcionario) {
        // Se o funcionário não usa Vale-Transporte, o desconto é zero.
        if (!funcionario.isValeTransporte()) {
            return 0.0;
        }

        // 1. Obtém os valores em BigDecimal
        BigDecimal salarioBruto = funcionario.getSalarioBruto();
        // Assume-se que valorVT é o valor total que a empresa paga/fornece.
        BigDecimal valorTotalVale = funcionario.getValorVT();

        // 2. Calcula o valor máximo de desconto permitido por lei (6% do salário bruto)
        BigDecimal descontoLegal = salarioBruto.multiply(PERCENTUAL_MAXIMO_VT)
                .setScale(2, RoundingMode.HALF_UP);

        // 3. Aplica o menor valor: ou o limite de 6% ou o valor total do vale.
        BigDecimal descontoFinal = descontoLegal.min(valorTotalVale);

        // Retorna o resultado como double (para satisfazer a interface)
        return descontoFinal.doubleValue();
    }
}