package bolsoseguroapi.Controller;

import bolsoseguroapi.Dto.Conta.ContaCadastroDTO;
import bolsoseguroapi.Dto.Conta.ContaGetDTO;
import bolsoseguroapi.Dto.Conta.ContaSaldoDTO;
import bolsoseguroapi.Dto.Conta.ContaUpdateDTO;
import bolsoseguroapi.Model.Conta;
import bolsoseguroapi.Service.ContaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor

@RestController
@RequestMapping("/contas")
public class ContaController {


    private final ContaService contaService;

    @PostMapping
    public ResponseEntity<Void> cadastrarConta(@RequestBody ContaCadastroDTO conta) {
        contaService.cadastrarConta(conta);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }



    @GetMapping
    public ResponseEntity<List<ContaGetDTO>> listarContas() {
        List<ContaGetDTO> contas = contaService.listarContasPorUsuario();
        return new ResponseEntity<>(contas, HttpStatus.OK);
    }


    @GetMapping("/saldo")
    public ResponseEntity<List<ContaSaldoDTO>> listarSaldoContas() {
        List<ContaSaldoDTO> contas = contaService.listarSaldoContas();
        return new ResponseEntity<>(contas, HttpStatus.OK);
    }



    @PutMapping("/{id}")
    public ResponseEntity<ContaUpdateDTO> atualizarConta(@PathVariable UUID id, @RequestBody ContaUpdateDTO dto) throws AccessDeniedException {
        ContaUpdateDTO contaAtualizada = contaService.atualizarConta(id, dto);
        return ResponseEntity.ok(contaAtualizada);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarConta(@PathVariable UUID id) throws AccessDeniedException {
        contaService.deletarConta(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}