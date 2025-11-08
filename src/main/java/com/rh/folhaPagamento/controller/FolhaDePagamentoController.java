package com.rh.folhaPagamento.controller;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.model.FolhaDePagamento;
import com.rh.folhaPagamento.repository.FolhaPagamentoRepository;
import com.rh.folhaPagamento.service.folhaPagamentoService;
import com.rh.folhaPagamento.service.FuncionarioService;
import com.rh.folhaPagamento.service.folhaPagamentoService.DetalheCalculo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/folha")
public class FolhaDePagamentoController {

    @Autowired
    private folhaPagamentoService folhaPagamentoService;

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private FolhaPagamentoRepository folhaPagamentoRepository;

    public static class DadosCalculoFolha {
        private Funcionario funcionario;
        private int diasUteis;
        public Funcionario getFuncionario() { return funcionario; }
        public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }
        public int getDiasUteis() { return diasUteis; }
        public void setDiasUteis(int diasUteis) { this.diasUteis = diasUteis; }
    }

    @PostMapping("/calcular")
    public Map<String, Object> calcular(@RequestBody DadosCalculoFolha dados) {
        
        // LocalDate hoje = LocalDate.now();
        DetalheCalculo r = folhaPagamentoService.calcularFolha(
                dados.getFuncionario(),
                dados.getDiasUteis(),
                hoje.getMonthValue(),
                hoje.getYear()
        );

        // Map<String, Object> resultado = new java.util.HashMap<>();
        resultado.put("salarioBase", r.salarioBase);
        resultado.put("salarioBruto", r.salarioBruto);
        resultado.put("totalAdicionais", r.totalAdicionais);
        resultado.put("totalBeneficios", r.totalBeneficios);
        resultado.put("totalDescontos", r.totalDescontos);
        resultado.put("salarioLiquido", r.salarioLiquido);
        resultado.put("totalAPagar", r.totalAPagar);
        resultado.put("descontoINSS", r.descontoINSS);
        resultado.put("descontoIRRF", r.descontoIRRF);

        // resultado.put("insalubridade", r.insalubridade);
        resultado.put("periculosidade", r.periculosidade);
        resultado.put("valeAlimentacao", r.valeAlimentacao);
        resultado.put("valeTransporte", r.valeTransporte);

        return resultado;
    }

    @GetMapping("/by-login/{login}")
    public List<FolhaDePagamento> folhasPorLogin(@PathVariable String login){
        return funcionarioService.buscarPorLogin(login)
                .map(folhaPagamentoRepository::findByFuncionario)
                .orElse(List.of());
    }

    @PostMapping("/gerar/{login}")
    public ResponseEntity<FolhaDePagamento> gerarFolhaEspecifica(@PathVariable String login,
                                                                 @RequestParam int mes,
                                                                 @RequestParam int ano,
                                                                 @RequestParam(required = false) Integer diasUteis){
        if(mes < 1 || mes > 12){
            return ResponseEntity.badRequest().build();
        }
        int dias = diasUteis != null ? diasUteis : 22;
        var funcOpt = funcionarioService.buscarPorLogin(login);
        if(funcOpt.isEmpty()) return ResponseEntity.notFound().build();
        var funcionario = funcOpt.get();

        var existente = folhaPagamentoRepository.findByFuncionarioAndMesReferenciaAndAnoReferencia(funcionario, mes, ano);
        if(existente.isPresent()){
            return ResponseEntity.ok(existente.get());
        }

        // DetalheCalculo det = folhaPagamentoService.calcularFolha(funcionario, dias, mes, ano);

        FolhaDePagamento fol = new FolhaDePagamento();
        fol.setFuncionario(funcionario);
        fol.setMesReferencia(mes);
        fol.setAnoReferencia(ano);
        fol.setSalarioBruto(det.salarioBruto);
        fol.setTotalAdicionais(det.totalAdicionais);
        fol.setTotalBeneficios(det.totalBeneficios);
        fol.setTotalDescontos(det.totalDescontos);
        fol.setSalarioLiquido(det.salarioLiquido);
        return ResponseEntity.ok(folhaPagamentoRepository.save(fol));
    }

    @PostMapping("/gerar-ultimos/{login}")
    public ResponseEntity<List<FolhaDePagamento>> gerarUltimosMeses(@PathVariable String login,
                                                                    @RequestParam(defaultValue = "6") int meses,
                                                                    @RequestParam(required = false) Integer diasUteis){
        if(meses < 1) meses = 1;
        int dias = diasUteis != null ? diasUteis : 22;
        var funcOpt = funcionarioService.buscarPorLogin(login);
        if(funcOpt.isEmpty()) return ResponseEntity.notFound().build();
        var funcionario = funcOpt.get();

        LocalDate cursor = LocalDate.now().minusMonths(1);
        List<FolhaDePagamento> geradas = new ArrayList<>();
        for(int i=0; i<meses; i++){
            int mes = cursor.getMonthValue();
            int ano = cursor.getYear();
            var existente = folhaPagamentoRepository.findByFuncionarioAndMesReferenciaAndAnoReferencia(funcionario, mes, ano);
            if(existente.isPresent()){
                geradas.add(existente.get());
            } else {

                // DetalheCalculo det = folhaPagamentoService.calcularFolha(funcionario, dias, mes, ano);

                FolhaDePagamento fol = new FolhaDePagamento();
                fol.setFuncionario(funcionario);
                fol.setMesReferencia(mes);
                fol.setAnoReferencia(ano);
                fol.setSalarioBruto(det.salarioBruto);
                fol.setTotalAdicionais(det.totalAdicionais);
                fol.setTotalBeneficios(det.totalBeneficios);
                fol.setTotalDescontos(det.totalDescontos);
                fol.setSalarioLiquido(det.salarioLiquido);
                geradas.add(folhaPagamentoRepository.save(fol));
            }
            cursor = cursor.minusMonths(1);
        }
        return ResponseEntity.ok(geradas);
    }
}