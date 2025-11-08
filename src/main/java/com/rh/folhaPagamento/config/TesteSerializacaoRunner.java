/*package com.rh.folhaPagamento.config; // Ou o pacote que você preferir

import com.rh.folhaPagamento.model.Usuario; // Vamos usar o Usuario para o teste
import com.rh.folhaPagamento.service.ArquivoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.io.File;

@Component
public class TesteSerializacaoRunner implements CommandLineRunner {

    private final ArquivoService arquivoService;
    private final String NOME_ARQUIVO_TESTE = "usuario_teste.dat";

    // O Spring vai injetar o seu ArquivoService automaticamente
    public TesteSerializacaoRunner(ArquivoService arquivoService) {
        this.arquivoService = arquivoService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=============================================");
        System.out.println("🚀 INICIANDO TESTE DE SERIALIZAÇÃO...");
        System.out.println("=============================================");

        // 1. Criar um objeto de teste
        Usuario usuarioOriginal = new Usuario();
        usuarioOriginal.setId(999);
        usuarioOriginal.setLogin("teste@serial");
        usuarioOriginal.setSenha("senha123");
        usuarioOriginal.setPermissao(2);

        System.out.println("Objeto Original: " + usuarioOriginal.getLogin());

        // 2. Serializar (Salvar)
        System.out.println("Serializando objeto...");
        arquivoService.serializar(usuarioOriginal, NOME_ARQUIVO_TESTE);

        // 3. Desserializar (Ler)
        System.out.println("Desserializando objeto...");
        Object objetoLido = arquivoService.desserializar(NOME_ARQUIVO_TESTE);

        // 4. Verificar o resultado
        if (objetoLido instanceof Usuario) {
            Usuario usuarioLido = (Usuario) objetoLido;
            System.out.println("Objeto Lido: " + usuarioLido.getLogin());

            // Verificação final
            if (usuarioOriginal.getLogin().equals(usuarioLido.getLogin()) &&
                    usuarioOriginal.getId().equals(usuarioLido.getId())) {
                System.out.println("✅ SUCESSO! Os objetos são iguais.");
            } else {
                System.out.println("❌ FALHA! Os objetos são diferentes.");
            }
        } else {
            System.out.println("❌ FALHA! O objeto lido não é do tipo Usuario.");
        }

        // 5. Limpar o arquivo de teste
        new File(NOME_ARQUIVO_TESTE).delete();
        System.out.println("Arquivo de teste limpo.");
        System.out.println("=============================================");
    }
}

*/