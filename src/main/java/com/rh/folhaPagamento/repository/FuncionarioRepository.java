package com.rh.folhaPagamento.repository;

import com.rh.folhaPagamento.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {
    Optional<Funcionario> findByUsuario_Login(String login);
    Optional<Funcionario> findByUsuario_Id(Integer usuarioId);
}
