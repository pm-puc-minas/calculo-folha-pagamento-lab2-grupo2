package com.rh.folhaPagamento;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.mockito.Mockito;

import com.rh.folhaPagamento.repository.FolhaPagamentoRepository;
import com.rh.folhaPagamento.repository.FuncionarioRepository;
import com.rh.folhaPagamento.repository.UsuarioRepository;

@SpringBootTest
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@Import(FolhaPagamentoApplicationTests.ConfigMock.class)
class FolhaPagamentoApplicationTests {

    @TestConfiguration
    static class ConfigMock {
        @Bean UsuarioRepository usuarioRepository(){ return Mockito.mock(UsuarioRepository.class); }
        @Bean FuncionarioRepository funcionarioRepository(){ return Mockito.mock(FuncionarioRepository.class); }
        @Bean FolhaPagamentoRepository folhaPagamentoRepository(){ return Mockito.mock(FolhaPagamentoRepository.class); }
    }

    @Test
    void contextLoads() {
    }
}
