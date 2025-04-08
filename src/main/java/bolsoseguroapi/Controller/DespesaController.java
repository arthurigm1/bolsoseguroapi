package bolsoseguroapi.Controller;



import bolsoseguroapi.Dto.Transacao.DespesaCartaoDTO;
import bolsoseguroapi.Dto.Transacao.DespesaDTO;
import bolsoseguroapi.Dto.Transacao.ReceitaDTO;
import bolsoseguroapi.Model.Enum.TipoPagamento;
import bolsoseguroapi.Service.DespesaService;
import bolsoseguroapi.Service.ReceitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/despesa")
@RequiredArgsConstructor
public class DespesaController {
    private final DespesaService despesaService;



    @PostMapping
    public ResponseEntity<DespesaDTO> adicionarDespesa(@RequestBody DespesaDTO despesaDTO) {
        if (despesaDTO.tipoPagamento() == TipoPagamento.CONTA && despesaDTO.contaId() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        if (despesaDTO.tipoPagamento() == TipoPagamento.CARTAO && despesaDTO.cartaoId() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        despesaService.adicionarDespesa(despesaDTO);
        return ResponseEntity.ok().build();
    }
    @GetMapping
    public ResponseEntity<List<DespesaCartaoDTO>> getDespesas(
            @RequestParam UUID cartaoId,
            @RequestParam int ano,
            @RequestParam int mes
    ) {
        List<DespesaCartaoDTO> despesas = despesaService.buscarDespesasPorCartaoEMes(cartaoId, ano, mes);
        return ResponseEntity.ok(despesas);
    }

}
