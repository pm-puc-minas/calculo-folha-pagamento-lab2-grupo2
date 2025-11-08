/*package com.rh.folhaPagamento.config; // Ou o pacote da sua aplicação principal

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.folhaPagamentoService; // Importe seu serviço
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate; // <-- ADICIONADO para pegar a data atual

@Component
public class TesteCacheFolhaRunner implements CommandLineRunner {

    private final folhaPagamentoService folhaService;

    // --- Dados de Teste ---
    private final Integer ID_TESTE = 999;
    private final int DIAS_UTEIS = 22;

    // ADICIONADO: mes e ano para o teste
    private final int MES_TESTE = LocalDate.now().getMonthValue();
    private final int ANO_TESTE = LocalDate.now().getYear();

    // CORRIGIDO: O nome do arquivo DEVE corresponder à nova lógica do serviço
    private final String NOME_ARQUIVO_CACHE = "folha_func_" + ID_TESTE + "_ano_" + ANO_TESTE + "_mes_" + MES_TESTE + ".dat";


    public TesteCacheFolhaRunner(folhaPagamentoService folhaService) {
        this.folhaService = folhaService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=============================================");
        System.out.println("🚀 INICIANDO TESTE DE CACHE DA FOLHA (CORRIGIDO)...");
        System.out.println("=============================================");

        // 1. Criar um funcionário de teste
        // (Preenchi campos básicos para os cálculos não falharem)
        Funcionario funcTeste = new Funcionario();
        funcTeste.setId(ID_TESTE);
        funcTeste.setNome("Funcionario Teste Cache");
        funcTeste.setSalarioBase(new BigDecimal("3000.00"));
        funcTeste.setAptoPericulosidade(false);
        funcTeste.setGrauInsalubridade(0);
        funcTeste.setValeAlimentacao(true);
        funcTeste.setValeTransporte(true);
        funcTeste.setDependentes(0);
        funcTeste.setSalarioBruto(BigDecimal.ZERO);
        funcTeste.setDescontoINSS(BigDecimal.ZERO);
        funcTeste.setValorVA(new BigDecimal("100.00"));
        funcTeste.setValorVT(new BigDecimal("100.00"));


        // 2. TESTE DE CACHE MISS (Primeira chamada)
        System.out.println("Limpando cache (se existir)...");
        new File(NOME_ARQUIVO_CACHE).delete();

        System.out.println("\n--- 1ª CHAMADA (ESPERADO: CACHE MISS) ---");
        // CORRIGIDO: Adicionado mes e ano
        folhaPagamentoService.DetalheCalculo resultadoMiss = folhaService.calcularFolha(funcTeste, DIAS_UTEIS, MES_TESTE, ANO_TESTE);
        System.out.println("Resultado do MISS: " + resultadoMiss.salarioLiquido);


        // 3. TESTE DE CACHE HIT (Segunda chamada)
        System.out.println("\n--- 2ª CHAMADA (ESPERADO: CACHE HIT) ---");
        // CORRIGIDO: Adicionado mes e ano
        folhaPagamentoService.DetalheCalculo resultadoHit = folhaService.calcularFolha(funcTeste, DIAS_UTEIS, MES_TESTE, ANO_TESTE);
        System.out.println("Resultado do HIT: " + resultadoHit.salarioLiquido);

        // 4. Verificação
        if (resultadoMiss.salarioLiquido.equals(resultadoHit.salarioLiquido)) {
            System.out.println("\n✅ SUCESSO! Os resultados 'MISS' e 'HIT' são idênticos.");
        } else {
            System.out.println("\n❌ FALHA! Os resultados são diferentes.");
        }

        // 5. Limpar o arquivo de teste
        new File(NOME_ARQUIVO_CACHE).delete();
        System.out.println("Arquivo de cache limpo.");
        System.out.println("=============================================");
    }
}

*/