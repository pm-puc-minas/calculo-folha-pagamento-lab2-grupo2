package com.rh.folhaPagamento.listener;

import com.rh.folhaPagamento.event.FuncionarioCriadoEvent;
import com.rh.folhaPagamento.model.Funcionario;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Esta classe é o 'Ouvinte' que reage ao evento.
@Component
public class LogFuncionarioListener {

    // Inicializa o sistema de Log do Spring
    private static final Logger logger = LoggerFactory.getLogger(LogFuncionarioListener.class);

    // O Spring chama este método automaticamente quando o evento é publicado.
    @EventListener
    public void onFuncionarioCriado(FuncionarioCriadoEvent event) {

        // 1. Pega os dados que o Service enviou no evento
        Funcionario funcionario = event.getFuncionario();

        // 2. Registra o Log (o requisito da Sprint)
        logger.info("=================================================");
        logger.info("EVENTO DISPARADO: Novo funcionário cadastrado.");
        // O ID pode ser null se o objeto ainda não foi salvo no banco, mas neste caso será o ID gerado.
        logger.info("ID do Funcionário: {}", funcionario.getId());
        logger.info("Nome: {}", funcionario.getNome());
        logger.info("Ação registrada em: {}", java.time.LocalDateTime.now());
        logger.info("=================================================");
    }
}