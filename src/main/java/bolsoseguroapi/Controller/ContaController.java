package bolsoseguroapi.Controller;

import bolsoseguroapi.Dto.Conta.ContaCadastroDTO;
import bolsoseguroapi.Dto.Conta.ContaGetDTO;
import bolsoseguroapi.Dto.Conta.ContaSaldoDTO;
import bolsoseguroapi.Model.Conta;
import bolsoseguroapi.Service.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @PostMapping
    public ResponseEntity<Conta> cadastrarConta(@RequestBody ContaCadastroDTO conta) {
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

    @GetMapping("/{id}")
    public ResponseEntity<Conta> buscarContaPorId(@PathVariable UUID id) {
        Optional<Conta> conta = contaService.buscarContaPorId(id);
        return conta.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Conta> atualizarConta(@PathVariable UUID id, @RequestBody Conta contaAtualizada) {
        Conta conta = contaService.atualizarConta(id, contaAtualizada);
        return new ResponseEntity<>(conta, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarConta(@PathVariable UUID id) {
        contaService.deletarConta(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}