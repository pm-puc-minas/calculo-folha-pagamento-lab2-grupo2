/*package com.rh.folhaPagamento.config; // Ou o pacote da sua aplicação

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.ArquivoService;
import com.rh.folhaPagamento.service.FuncionarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigDecimal;

@Component
public class TesteFuncionarioServiceRunner implements CommandLineRunner {

    private final FuncionarioService funcionarioService;
    private final ArquivoService arquivoService;

    private final Integer ID_TESTE = 888;
    private final String NOME_ARQUIVO = "funcionario_" + ID_TESTE + ".dat";

    public TesteFuncionarioServiceRunner(FuncionarioService funcionarioService, ArquivoService arquivoService) {
        this.funcionarioService = funcionarioService;
        this.arquivoService = arquivoService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=============================================");
        System.out.println("🚀 INICIANDO TESTE DO FUNCIONARIO SERVICE...");
        System.out.println("=============================================");

        // 1. Criar um funcionário de teste
        Funcionario funcOriginal = new Funcionario();
        funcOriginal.setId(ID_TESTE);
        funcOriginal.setNome("Funcionario de Teste");
        funcOriginal.setCpf("12345678900");
        funcOriginal.setSalarioBase(new BigDecimal("5000.00"));

        // 2. Limpar cache e Serializar o objeto manualmente
        new File(NOME_ARQUIVO).delete();
        System.out.println("Serializando objeto de teste em: " + NOME_ARQUIVO);
        arquivoService.serializar(funcOriginal, NOME_ARQUIVO);

        // 3. Chamar o método de DESSERIALIZAÇÃO do serviço
        System.out.println("Serviço buscando funcionário do arquivo...");
        Funcionario funcLido = funcionarioService.buscarFuncionarioDoArquivo(ID_TESTE);

        // 4. Verificação
        if (funcLido != null && funcLido.getId().equals(funcOriginal.getId()) && funcLido.getNome().equals(funcOriginal.getNome())) {
            System.out.println("✅ SUCESSO! FuncionarioService desserializou o objeto corretamente.");
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