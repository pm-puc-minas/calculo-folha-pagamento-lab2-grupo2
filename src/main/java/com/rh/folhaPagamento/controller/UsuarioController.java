package com.rh.folhaPagamento.controller;

import com.rh.folhaPagamento.dto.UsuarioResponseDTO;
import com.rh.folhaPagamento.model.FolhaDePagamento;
import com.rh.folhaPagamento.model.Funcionario;
import com.rh.folhaPagamento.model.Usuario;
import com.rh.folhaPagamento.repository.FolhaPagamentoRepository;
import com.rh.folhaPagamento.repository.FuncionarioRepository;
import com.rh.folhaPagamento.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.rh.folhaPagamento.service.folhaPagamentoService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final FolhaPagamentoRepository folhaPagamentoRepository;
    private final folhaPagamentoService folhaPagamentoService;

    public UsuarioController(UsuarioRepository usuarioRepository, FuncionarioRepository funcionarioRepository, FolhaPagamentoRepository folhaPagamentoRepository, folhaPagamentoService folhaPagamentoService) {
        this.usuarioRepository = usuarioRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.folhaPagamentoRepository = folhaPagamentoRepository;
        this.folhaPagamentoService = folhaPagamentoService;
    }

    @GetMapping
    public List<UsuarioResponseDTO> listarTodos(){
        return usuarioRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obter(@PathVariable Integer id){
        return usuarioRepository.findById(id).map(u -> ResponseEntity.ok(toDto(u))).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/permissao/{nivel}")
    public List<UsuarioResponseDTO> listarPorPermissao(@PathVariable int nivel){
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getPermissao() == nivel)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizarParcial(@PathVariable Integer id, @RequestBody Map<String, Object> body){
        Optional<Usuario> opt = usuarioRepository.findById(id);
        if(opt.isEmpty()) return ResponseEntity.notFound().build();
        Usuario u = opt.get();
        if(body.containsKey("login")) u.setLogin(String.valueOf(body.get("login")));
        if(body.containsKey("senha")) u.setSenha(String.valueOf(body.get("senha")));
        if(body.containsKey("permissao")) u.setPermissao(Integer.parseInt(String.valueOf(body.get("permissao"))));
        usuarioRepository.save(u);
        return ResponseEntity.ok(toDto(u));
    }

    @GetMapping("/{id}/funcionario")
    public ResponseEntity<Funcionario> obterFuncionario(@PathVariable Integer id){
        Optional<Funcionario> f = funcionarioRepository.findByUsuario_Id(id);
        return f.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/funcionario")
    public ResponseEntity<Funcionario> atualizarFuncionario(@PathVariable Integer id, @RequestBody Map<String, Object> body){
        Optional<Funcionario> fOpt = funcionarioRepository.findByUsuario_Id(id);
        if(fOpt.isEmpty()) return ResponseEntity.notFound().build();
        Funcionario f = fOpt.get();
        if(body.containsKey("nome")) f.setNome(String.valueOf(body.get("nome")));
        if(body.containsKey("cpf")) {
            String cpf = String.valueOf(body.get("cpf"));
            f.setCpf(cpf != null ? cpf.replaceAll("\\D", "") : null);
        }
        if(body.containsKey("cargo")) f.setCargo(String.valueOf(body.get("cargo")));
        if(body.containsKey("dependentes")) f.setDependentes(Integer.parseInt(String.valueOf(body.get("dependentes"))));
        if(body.containsKey("salarioBase")) f.setSalarioBase(new BigDecimal(String.valueOf(body.get("salarioBase"))));
        if(body.containsKey("aptoPericulosidade")) f.setAptoPericulosidade(Boolean.parseBoolean(String.valueOf(body.get("aptoPericulosidade"))));
        if(body.containsKey("grauInsalubridade")) f.setGrauInsalubridade(Integer.parseInt(String.valueOf(body.get("grauInsalubridade"))));
        if(body.containsKey("valeTransporte")) f.setValeTransporte(Boolean.parseBoolean(String.valueOf(body.get("valeTransporte"))));
        if(body.containsKey("valeAlimentacao")) f.setValeAlimentacao(Boolean.parseBoolean(String.valueOf(body.get("valeAlimentacao"))));
        if(body.containsKey("valorVT")) f.setValorVT(new BigDecimal(String.valueOf(body.get("valorVT"))));
        if(body.containsKey("valorVA")) f.setValorVA(new BigDecimal(String.valueOf(body.get("valorVA"))));

        int diasUteis = body.containsKey("diasUteis") ? Integer.parseInt(String.valueOf(body.get("diasUteis"))) : 22;


        LocalDate hoje = LocalDate.now();
        var det = folhaPagamentoService.calcularFolha(f, diasUteis, hoje.getMonthValue(), hoje.getYear());

        funcionarioRepository.save(f);

        // A variável 'hoje' já foi definida acima
        folhaPagamentoRepository.findByFuncionarioAndMesReferenciaAndAnoReferencia(f, hoje.getMonthValue(), hoje.getYear())
                .ifPresent(fp -> {
                    fp.setSalarioBruto(det.salarioBruto);
                    fp.setTotalAdicionais(det.totalAdicionais);
                    fp.setTotalBeneficios(det.totalBeneficios);
                    fp.setTotalDescontos(det.totalDescontos);
                    fp.setSalarioLiquido(det.salarioLiquido);
                    folhaPagamentoRepository.save(fp);
                });

        return ResponseEntity.ok(f);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Integer id){
        Optional<Usuario> uOpt = usuarioRepository.findById(id);
        if(uOpt.isEmpty()) return ResponseEntity.notFound().build();
        Optional<Funcionario> fOpt = funcionarioRepository.findByUsuario_Id(id);
        if(fOpt.isPresent()){
            Funcionario f = fOpt.get();
            List<FolhaDePagamento> folhas = folhaPagamentoRepository.findByFuncionario(f);
            if(!folhas.isEmpty()) folhaPagamentoRepository.deleteAll(folhas);
            funcionarioRepository.delete(f);
        }
        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private UsuarioResponseDTO toDto(Usuario u){
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(u.getId());
        dto.setLogin(u.getLogin());
        dto.setPermissao(u.getPermissao());
        return dto;
    }
}