package com.rh.folhaPagamento.controller;


import com.rh.folhaPagamento.dto.LoginRequestDTO;
import com.rh.folhaPagamento.service.GestaoAcessoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private GestaoAcessoService gestaoAcessoService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO request){
        try{
            gestaoAcessoService.auth(request.getLogin(), request.getSenha());
            return ResponseEntity.ok("Login efetuado com sucesso");
        } catch (RuntimeException e){
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
