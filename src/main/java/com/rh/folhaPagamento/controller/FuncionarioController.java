package com.rh.folhaPagamento.controller;

import com.rh.folhaPagamento.dto.FuncionarioRequestDTO;
import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.model.FolhaDePagamento;
import com.rh.folhaPagamento.model.Usuario;
import com.rh.folhaPagamento.repository.FolhaPagamentoRepository;
import com.rh.folhaPagamento.repository.FuncionarioRepository;
import com.rh.folhaPagamento.repository.UsuarioRepository;
import com.rh.folhaPagamento.service.FuncionarioService;
import com.rh.folhaPagamento.service.folhaPagamentoService;
import com.rh.folhaPagamento.service.folhaPagamentoService.DetalheCalculo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FolhaPagamentoRepository folhaPagamentoRepository;

    @Autowired
    private folhaPagamentoService folhaService;

    @PostMapping
    public ResponseEntity<Funcionario> criar(@RequestBody FuncionarioRequestDTO dto) {
        try {
            Funcionario funcionario = funcionarioService.criarFuncionario(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(funcionario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public List<Funcionario> listarTodos() {
        List<Funcionario> lista = funcionarioService.listarTodos();
        return lista.stream()
                .filter(f -> f.getUsuario() == null || f.getUsuario().getPermissao() != 2)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Funcionario> buscarPorId(@PathVariable Integer id) {
        return funcionarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-login/{login}")
    public ResponseEntity<Funcionario> buscarPorLogin(@PathVariable String login) {
        return funcionarioService.buscarPorLogin(login)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Funcionario> atualizarParcial(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        try {
            Funcionario atualizado = funcionarioService.atualizarParcial(id, body);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        var opt = funcionarioRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Funcionario f = opt.get();
        List<FolhaDePagamento> folhas = folhaPagamentoRepository.findByFuncionario(f);
        folhaPagamentoRepository.deleteAll(folhas);
        funcionarioRepository.delete(f);
        Usuario u = f.getUsuario();
        if (u != null && u.getPermissao() != 2) {
            usuarioRepository.delete(u);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idFuncionario}/folhas")
    public ResponseEntity<List<FolhaDePagamento>> listarFolhas(@PathVariable Integer idFuncionario) {
        var opt = funcionarioRepository.findById(idFuncionario);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<FolhaDePagamento> folhas = folhaPagamentoRepository.findByFuncionario(opt.get());
        return ResponseEntity.ok(folhas);
    }

    @PostMapping("/{idFuncionario}/folhas")
    public ResponseEntity<FolhaDePagamento> criarFolha(@PathVariable Integer idFuncionario,
                                                       @RequestBody Map<String, Object> body) {
        int mes = Integer.parseInt(String.valueOf(body.getOrDefault("mes", 0)));
        int ano = Integer.parseInt(String.valueOf(body.getOrDefault("ano", 0)));
        int diasUteis = Integer.parseInt(String.valueOf(body.getOrDefault("diasUteis", 22)));
        if (mes < 1 || mes > 12 || ano <= 0) {
            return ResponseEntity.badRequest().build();
        }
        var opt = funcionarioRepository.findById(idFuncionario);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Funcionario funcionario = opt.get();
        var existente = folhaPagamentoRepository
                .findByFuncionarioAndMesReferenciaAndAnoReferencia(funcionario, mes, ano);
        if (existente.isPresent()) {
            return ResponseEntity.ok(existente.get());
        }
        DetalheCalculo det = folhaService.calcularFolha(funcionario, diasUteis, mes, ano);
        FolhaDePagamento fol = new FolhaDePagamento();
        fol.setFuncionario(funcionario);
        fol.setMesReferencia(mes);
        fol.setAnoReferencia(ano);
        fol.setSalarioBruto(det.salarioBruto);
        fol.setTotalAdicionais(det.totalAdicionais);
        fol.setTotalBeneficios(det.totalBeneficios);
        fol.setTotalDescontos(det.totalDescontos);
        fol.setSalarioLiquido(det.salarioLiquido);
        fol.setInsalubridade(det.insalubridade);
        fol.setPericulosidade(det.periculosidade);
        fol.setValeAlimentacao(det.valeAlimentacao);
        fol.setValeTransporte(det.valeTransporte);
        fol.setInss(det.descontoINSS);
        fol.setIrrf(det.descontoIRRF);
        FolhaDePagamento salva = folhaPagamentoRepository.save(fol);
        return ResponseEntity.ok(salva);
    }

    @PutMapping("/{idFuncionario}/folhas/{idFolha}")
    public ResponseEntity<FolhaDePagamento> atualizarFolha(@PathVariable Integer idFuncionario,
                                                           @PathVariable Integer idFolha,
                                                           @RequestBody Map<String, Object> body) {
        int diasUteis = Integer.parseInt(String.valueOf(body.getOrDefault("diasUteis", 22)));
        var opt = folhaPagamentoRepository.findById(idFolha);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        FolhaDePagamento folha = opt.get();
        if (!folha.getFuncionario().getId().equals(idFuncionario)) {
            return ResponseEntity.badRequest().build();
        }
        Funcionario f = folha.getFuncionario();
        int mes = folha.getMesReferencia();
        int ano = folha.getAnoReferencia();
        DetalheCalculo det = folhaService.calcularFolha(f, diasUteis, mes, ano);
        folha.setSalarioBruto(det.salarioBruto);
        folha.setTotalAdicionais(det.totalAdicionais);
        folha.setTotalBeneficios(det.totalBeneficios);
        folha.setTotalDescontos(det.totalDescontos);
        folha.setSalarioLiquido(det.salarioLiquido);
        folha.setInsalubridade(det.insalubridade);
        folha.setPericulosidade(det.periculosidade);
        folha.setValeAlimentacao(det.valeAlimentacao);
        folha.setValeTransporte(det.valeTransporte);
        folha.setInss(det.descontoINSS);
        folha.setIrrf(det.descontoIRRF);
        FolhaDePagamento salva = folhaPagamentoRepository.save(folha);
        return ResponseEntity.ok(salva);
    }

    @DeleteMapping("/{idFuncionario}/folhas/{idFolha}")
    public ResponseEntity<Void> excluirFolha(@PathVariable Integer idFuncionario,
                                             @PathVariable Integer idFolha) {
        var opt = folhaPagamentoRepository.findById(idFolha);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        FolhaDePagamento folha = opt.get();
        if (!folha.getFuncionario().getId().equals(idFuncionario)) {
            return ResponseEntity.badRequest().build();
        }
        folhaPagamentoRepository.delete(folha);
        return ResponseEntity.noContent().build();
    }
}
