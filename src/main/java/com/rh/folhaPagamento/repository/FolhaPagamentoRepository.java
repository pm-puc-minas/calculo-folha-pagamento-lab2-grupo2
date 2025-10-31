package com.rh.folhaPagamento.repository;

import com.rh.folhaPagamento.model.FolhaDePagamento;
import com.rh.folhaPagamento.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FolhaPagamentoRepository extends JpaRepository<FolhaDePagamento, Integer> {
    List<FolhaDePagamento> findByFuncionario(Funcionario funcionario);
    Optional<FolhaDePagamento> findByFuncionarioAndMesReferenciaAndAnoReferencia(Funcionario funcionario, Integer mesReferencia, Integer anoReferencia);
}
