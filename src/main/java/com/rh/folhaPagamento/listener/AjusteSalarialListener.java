package com.rh.folhaPagamento.listener;

import com.rh.folhaPagamento.event.AjusteSalarialEvent; // Importa a Mensagem do Evento
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Componente que escuta o AjusteSalarialEvent e executa uma ação em background (log de auditoria).
 */
@Component
public class AjusteSalarialListener {

    private static final Logger logger = LoggerFactory.getLogger(AjusteSalarialListener.class);

    /**
     * Método que é chamado automaticamente pelo Spring ao receber o AjusteSalarialEvent.
     * * @param event O evento disparado pelo FuncionarioService.
     */
    @EventListener
    public void handleAjusteSalarialEvent(AjusteSalarialEvent event) {

        // --- AÇÃO: Log de Auditoria ---
        // Na prática, esta lógica não atrasa a resposta da API ao usuário.

        logger.info("==========================================================");
        logger.info("EVENTO DISPARADO: AJUSTE SALARIAL DE AUDITORIA");
        logger.info("Funcionario ID: {}", event.getFuncionario().getId());
        logger.info("Nome: {}", event.getFuncionario().getNome());
        logger.info("Salário ANTERIOR: R$ {}", event.getSalarioAntigo());
        logger.info("Salário ATUAL: R$ {}", event.getNovoSalario());
        logger.info("Ajuste registrado com sucesso para fins de auditoria.");
        logger.info("==========================================================");
    }
}