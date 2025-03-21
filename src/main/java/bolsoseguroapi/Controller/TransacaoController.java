package bolsoseguroapi.Controller;

import bolsoseguroapi.Dto.Transacao.BalancoMensalDetalhadoDTO;
import bolsoseguroapi.Dto.Transacao.TransacaoDTO;
import bolsoseguroapi.Service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;


    @GetMapping("")
    public List<TransacaoDTO> obterUltimasTransacoes() {
        return transacaoService.obterUltimasTransacoesDeTodasContas();
    }

    @GetMapping("/despesa")
    public BigDecimal getTotalDespesasMes() {
        return transacaoService.obterTotalDespesasMes();
    }
    @GetMapping("/receita")
    public BigDecimal getTotalReceitasMes() {
        return transacaoService.obterTotalReceitasMes();
    }

    @GetMapping("/ultimos-meses")
    public ResponseEntity<List<BalancoMensalDetalhadoDTO>> obterBalancoUltimosMeses() {
        return ResponseEntity.ok(transacaoService.obterBalancoUltimosMeses());
    }
    @GetMapping("/investimento")
    public BigDecimal obterSaldoPorCategoria() {
        return transacaoService.calcularSaldoCategoria();
    }
}