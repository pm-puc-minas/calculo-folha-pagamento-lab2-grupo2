package com.rh.folhaPagamento.controller;


import com.rh.folhaPagamento.dto.LoginRequestDTO;
import com.rh.folhaPagamento.dto.UsuarioResponseDTO;
import com.rh.folhaPagamento.model.Usuario;
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
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request){
        try{
            Usuario usuario = gestaoAcessoService.authenticate(request.getLogin(), request.getSenha());
            UsuarioResponseDTO dto = new UsuarioResponseDTO();
            dto.setId(usuario.getId());
            dto.setLogin(usuario.getLogin());
            dto.setPermissao(usuario.getPermissao());
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e){
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}

//Teste comentario
