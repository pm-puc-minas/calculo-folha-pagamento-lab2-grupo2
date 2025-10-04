package com.rh.folhaPagamento.controller;

import com.rh.folhaPagamento.dto.FuncionarioRequestDTO;
import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {
    @Autowired
    private FuncionarioService funcionarioService;


    @PostMapping
    public ResponseEntity<Funcionario> cadastrarFuncionario(@RequestBody FuncionarioRequestDTO request) {
        Funcionario novoFuncionario = funcionarioService.criarFuncionario(request);
        return ResponseEntity.status(201).body(novoFuncionario);
    }

    //Get, put...
}
