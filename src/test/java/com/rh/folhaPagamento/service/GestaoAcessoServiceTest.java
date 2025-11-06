package com.rh.folhaPagamento.service;

import com.rh.folhaPagamento.model.Usuario;
import com.rh.folhaPagamento.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class GestaoAcessoServiceTest {

    
    @InjectMocks
    private GestaoAcessoService gestaoAcessoService;

    
    @Mock
    private UsuarioRepository usuarioRepository;

    private final String LOGIN_CORRETA = "admin.user";
    private final String SENHA_CORRETA = "senha123";

    private Usuario usuarioCorreto;

    @BeforeEach
    void setUp() {
        
        usuarioCorreto = new Usuario();
        usuarioCorreto.setId(1);
    usuarioCorreto.setLogin(LOGIN_CORRETA);
        usuarioCorreto.setSenha(SENHA_CORRETA); 
    }


    @Test
    @DisplayName("Teste 1: Deve autenticar com sucesso e retornar o Usuario")
    void authenticate_Sucesso() {
        
    when(usuarioRepository.findByLogin(LOGIN_CORRETA)).thenReturn(Optional.of(usuarioCorreto));

        
        Usuario resultado = gestaoAcessoService.authenticate(LOGIN_CORRETA, SENHA_CORRETA);

        
        assertNotNull(resultado);
        assertEquals(LOGIN_CORRETA, resultado.getLogin());
        
        
        verify(usuarioRepository, times(1)).findByLogin(LOGIN_CORRETA);
    }

    @Test
    @DisplayName("Teste 2: Deve lançar RuntimeException quando o usuário não for encontrado")
    void authenticate_UsuarioNaoEncontrado() {
        
        when(usuarioRepository.findByLogin(anyString())).thenReturn(Optional.empty());

        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            gestaoAcessoService.authenticate("usuario.errado", SENHA_CORRETA);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioRepository, times(1)).findByLogin(anyString());
    }

    @Test
    @DisplayName("Teste 3: Deve lançar RuntimeException quando a senha estiver incorreta")
    void authenticate_SenhaIncorreta() {
        final String SENHA_INCORRETA = "senhaerrada";
        
        
        when(usuarioRepository.findByLogin(LOGIN_CORRETA)).thenReturn(Optional.of(usuarioCorreto));

        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            gestaoAcessoService.authenticate(LOGIN_CORRETA, SENHA_INCORRETA);
        });

        assertEquals("Senha incorreta", exception.getMessage());
        verify(usuarioRepository, times(1)).findByLogin(LOGIN_CORRETA);
    }


    @Test
    @DisplayName("Teste 4: Deve executar auth com sucesso e não lançar exceção")
    void auth_Sucesso() {
        
    when(usuarioRepository.findByLogin(LOGIN_CORRETA)).thenReturn(Optional.of(usuarioCorreto));

        
        assertDoesNotThrow(() -> {
            gestaoAcessoService.auth(LOGIN_CORRETA, SENHA_CORRETA);
        });
        
        
        verify(usuarioRepository, times(1)).findByLogin(LOGIN_CORRETA);
    }
}
