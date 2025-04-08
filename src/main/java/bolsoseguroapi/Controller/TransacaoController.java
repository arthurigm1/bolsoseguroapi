package bolsoseguroapi.Controller;

import bolsoseguroapi.Dto.Transacao.BalancoMensalDetalhadoDTO;
import bolsoseguroapi.Dto.Transacao.TransacaoDTO;
import bolsoseguroapi.Dto.Transacao.TransacaoDetalhadaDTO;
import bolsoseguroapi.Service.TransacaoService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/saldosMes")
    public ResponseEntity<List<TransacaoDetalhadaDTO>> obterTransacoesPorMes(
            @RequestParam int mes,
            @RequestParam int ano) {

        List<TransacaoDetalhadaDTO> transacoes = transacaoService.obterTransacoesPorMes(mes, ano);
        return ResponseEntity.ok(transacoes);
    }
    @GetMapping("/relatorio")
    public ResponseEntity<byte[]> gerarRelatorioTransacaoMensal(       @RequestParam @Min(1) @Max(12) int mes,
                                                                       @RequestParam @Min(2000) int ano) {
        try {
            byte[] pdfBytes = transacaoService.gerarRelatorioTransacaoMensal(mes,ano);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=relatorio_mes.pdf")
                    .body(pdfBytes);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}