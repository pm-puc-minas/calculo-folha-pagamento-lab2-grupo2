package com.rh.folhaPagamento.event;

import com.rh.folhaPagamento.model.Funcionario;
import org.springframework.context.ApplicationEvent;

// Esta é a classe que representa o fato de um funcionário ter sido criado.
public class FuncionarioCriadoEvent extends ApplicationEvent {

    private final Funcionario funcionario;

    /**
     * Construtor padrão para eventos.
     * @param source O objeto que disparou o evento (geralmente 'this' do Service).
     * @param funcionario O funcionário recém-criado.
     */
    public FuncionarioCriadoEvent(Object source, Funcionario funcionario) {
        super(source);
        this.funcionario = funcionario;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }
}