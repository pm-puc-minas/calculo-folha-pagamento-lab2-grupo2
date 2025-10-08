package com.rh.folhaPagamento.controller;

import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.service.folhaPagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;

@RestController
@RequestMapping("/folha")
public class FolhaDePagamentoController {

    @Autowired
    private folhaPagamentoService folhaPagamentoService;

    public static class DadosCalculoFolha {
        private Funcionario funcionario;
        private int diasUteis;

        public Funcionario getFuncionario() {
            return funcionario;
        }

        public void setFuncionario(Funcionario funcionario) {
            this.funcionario = funcionario;
        }

        public int getDiasUteis() {
            return diasUteis;
        }

        public void setDiasUteis(int diasUteis) {
            this.diasUteis = diasUteis;
        }
    }

    @PostMapping("/calcular")
    public BigDecimal calcularSalario(@RequestBody DadosCalculoFolha dados) {

        return folhaPagamentoService.calcularFolha(dados.getFuncionario(), dados.getDiasUteis());
    }
}