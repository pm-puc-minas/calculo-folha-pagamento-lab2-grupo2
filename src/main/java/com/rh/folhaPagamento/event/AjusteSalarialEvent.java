package com.rh.folhaPagamento.event;

import com.rh.folhaPagamento.model.Funcionario;
import org.springframework.context.ApplicationEvent;
import java.math.BigDecimal;

// Estende ApplicationEvent para que o Spring reconheça isso como um evento.
public class AjusteSalarialEvent extends ApplicationEvent {

    // Final é usado para garantir que o estado do evento não mude após ser criado.
    private final Funcionario funcionario;
    private final BigDecimal salarioAntigo;
    private final BigDecimal novoSalario;

    // Construtor: usado para criar a mensagem do evento com todos os dados necessários.
    public AjusteSalarialEvent(Object source, Funcionario funcionario, BigDecimal salarioAntigo, BigDecimal novoSalario) {
        // A chamada a 'super(source)' é obrigatória para a ApplicationEvent.
        super(source);
        this.funcionario = funcionario;
        this.salarioAntigo = salarioAntigo;
        this.novoSalario = novoSalario;
    }

    // Getters: para que o Listener possa acessar as informações.
    public Funcionario getFuncionario() {
        return funcionario;
    }

    public BigDecimal getSalarioAntigo() {
        return salarioAntigo;
    }

    public BigDecimal getNovoSalario() {
        return novoSalario;
    }
}