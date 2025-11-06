package com.rh.folhaPagamento.controller;

import com.rh.folhaPagamento.dto.FuncionarioRequestDTO;
// Importa o DTO que criamos para o Ajuste Salarial
import com.rh.folhaPagamento.dto.AjusteSalarialRequestDTO;
import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    @Autowired
    private FuncionarioService funcionarioService;

    // =========================================================================
    // 1. CADASTRAR FUNCIONÁRIO (POST)
    // =========================================================================
    @PostMapping
    public ResponseEntity<?> cadastrarFuncionario(@RequestBody FuncionarioRequestDTO request) {
        try {
            Funcionario novoFuncionario = funcionarioService.criarFuncionario(request);
            return ResponseEntity.status(201).body(novoFuncionario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body("Login ou CPF já existente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao criar funcionário");
        }
    }

    // =========================================================================
    // 2. BUSCAR POR LOGIN (GET)
    // =========================================================================
    @GetMapping("/by-login/{login}")
    public ResponseEntity<Funcionario> buscarPorLogin(@PathVariable String login){
        return funcionarioService.buscarPorLogin(login)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================================================================
    // 3. AJUSTAR SALÁRIO (PUT) - Implementação de Eventos
    //    Retorna ResponseEntity<?> para lidar com Funcionario (sucesso) ou String (erro)
    // =========================================================================
    @PutMapping("/{id}/ajuste-salarial")
    public ResponseEntity<?> ajustarSalario(
            @PathVariable Integer id,
            @RequestBody AjusteSalarialRequestDTO dto) {

        try {
            // Validação de negócio básica
            if (dto.getNovoSalario() == null || dto.getNovoSalario().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("O novo salário deve ser um valor positivo.");
            }

            Funcionario funcionarioAtualizado = funcionarioService.atualizarSalario(id, dto.getNovoSalario());

            // Retorna o objeto Funcionario com status 200 OK
            return ResponseEntity.ok(funcionarioAtualizado);

        } catch (IllegalArgumentException e) {
            // Captura erro se o funcionário não for encontrado (do .orElseThrow no Service)
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao processar ajuste salarial: " + e.getMessage());
        }
    }

    // =========================================================================
    // 4. LISTAR TODOS (GET) - Implementação de Coleções/Streams (List)
    // =========================================================================
    @GetMapping
    public List<Funcionario> listarTodos() {
        return funcionarioService.listarTodos();
    }

    // =========================================================================
    // 5. OBTER MAPA DE FUNCIONÁRIOS (GET) - Implementação de Coleções/Streams (Map)
    // =========================================================================
    @GetMapping("/map")
    public Map<Integer, Funcionario> getFuncionariosMap() {
        return funcionarioService.getFuncionariosMap();
    }
}