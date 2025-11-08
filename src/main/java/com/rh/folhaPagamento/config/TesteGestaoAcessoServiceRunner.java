/*(package com.rh.folhaPagamento.config; // Ou o pacote da sua aplicação

import com.rh.folhaPagamento.model.Usuario;
import com.rh.folhaPagamento.service.ArquivoService;
import com.rh.folhaPagamento.service.GestaoAcessoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class TesteGestaoAcessoServiceRunner implements CommandLineRunner {

    private final GestaoAcessoService gestaoAcessoService;
    private final ArquivoService arquivoService;

    private final Integer ID_TESTE = 777;
    private final String NOME_ARQUIVO = "usuario_" + ID_TESTE + ".dat";

    public TesteGestaoAcessoServiceRunner(GestaoAcessoService gestaoAcessoService, ArquivoService arquivoService) {
        this.gestaoAcessoService = gestaoAcessoService;
        this.arquivoService = arquivoService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=============================================");
        System.out.println("🚀 INICIANDO TESTE DO GESTAO ACESSO SERVICE...");
        System.out.println("=============================================");

        // 1. Criar um usuário de teste
        Usuario usuarioOriginal = new Usuario();
        usuarioOriginal.setId(ID_TESTE);
        usuarioOriginal.setLogin("usuario@teste.com");
        usuarioOriginal.setSenha("senhaSuperSecreta");
        usuarioOriginal.setPermissao(1);

        // 2. Limpar cache e Serializar o objeto manualmente
        new File(NOME_ARQUIVO).delete();
        System.out.println("Serializando objeto de teste em: " + NOME_ARQUIVO);
        arquivoService.serializar(usuarioOriginal, NOME_ARQUIVO);

        // 3. Chamar o método de DESSERIALIZAÇÃO do serviço
        System.out.println("Serviço buscando usuário do arquivo...");
        Usuario usuarioLido = gestaoAcessoService.buscarUsuarioDoArquivo(ID_TESTE);

        // 4. Verificação
        if (usuarioLido != null && usuarioLido.getId().equals(usuarioOriginal.getId()) && usuarioLido.getLogin().equals(usuarioOriginal.getLogin())) {
            System.out.println("✅ SUCESSO! GestaoAcessoService desserializou o objeto corretamente.");
        } else {
            System.out.println("❌ FALHA! O objeto lido é diferente do original.");
        }

        // 5. Limpar
        new File(NOME_ARQUIVO).delete();
        System.out.println("Arquivo de teste limpo.");
        System.out.println("=============================================");
    }
}

*/