
package com.rh.folhaPagamento.strategy;

import java.util.List;
import org.springframework.stereotype.Service;
import com.rh.folhaPagamento.model.Funcionario;

@Service // Marca como um componente de serviço que será usado por outras classes
public class CalculoFolhaContext {

    // Lista para receber TODAS as classes que implementam a Strategy (INSS, IRRF, VT)
    private final List<CalculoDescontoStrategy> strategies;

    // Construtor
    public CalculoFolhaContext(List<CalculoDescontoStrategy> strategies) {
        this.strategies = strategies;
    }

    /**
     * Itera sobre todas as estratégias injetadas e calcula o total dos descontos.
     * Este método é o ponto de entrada para a folha de pagamento.
     */
    public double calcularTotalDescontos(Funcionario funcionario) {
        double totalDescontos = 0.0;

        // Percorre a lista de estratégias: INSS, IRRF, VT, etc.
        for (CalculoDescontoStrategy strategy : strategies) {
            // Chama o método calcular de cada estratégia e acumula o total
            totalDescontos += strategy.calcular(funcionario);
        }

        return totalDescontos;
    }
}