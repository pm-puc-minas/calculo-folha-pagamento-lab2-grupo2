package com.rh.folhaPagamento.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    private String Login;
    private String Senha;
    private int permissao;
}
