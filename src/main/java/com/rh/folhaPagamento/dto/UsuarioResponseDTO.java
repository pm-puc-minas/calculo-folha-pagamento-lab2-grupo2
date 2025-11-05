package com.rh.folhaPagamento.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioResponseDTO {
    private Integer id;
    private String login;
    private int permissao;
}

