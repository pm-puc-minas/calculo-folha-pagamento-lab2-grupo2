package com.rh.folhaPagamento.controller;

import com.rh.folhaPagamento.dto.FuncionarioRequestDTO;
import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {
    @Autowired
    private FuncionarioService funcionarioService;


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

    @GetMapping("/by-login/{login}")
    public ResponseEntity<Funcionario> buscarPorLogin(@PathVariable String login){
        return funcionarioService.buscarPorLogin(login)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
