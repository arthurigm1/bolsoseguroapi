package bolsoseguroapi.Controller;

import bolsoseguroapi.Dto.Cartao.FaturaCartaoDTO;
import bolsoseguroapi.Dto.Cartao.PagamentoFaturaRequest;
import bolsoseguroapi.Exceptions.RelatorioException;
import bolsoseguroapi.Model.FaturaCartao;
import bolsoseguroapi.Service.FaturaService;
import bolsoseguroapi.Validador.DataValidador;
import com.itextpdf.text.DocumentException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/faturas")
public class FaturaController {

    @Autowired
    private FaturaService faturaService;

    @GetMapping("/{cartaoId}/{mes}/{ano}")
    public ResponseEntity<List<FaturaCartaoDTO>> getFaturasPorMes(
            @PathVariable UUID cartaoId,
            @PathVariable int mes,
            @PathVariable int ano) {

        List<FaturaCartaoDTO> faturas = faturaService.buscarFaturasPorMes(cartaoId, mes, ano);

        if (faturas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(faturas);
    }

    @PutMapping("/pagar")
    public ResponseEntity<String> pagarFatura(
          @Valid @RequestBody PagamentoFaturaRequest request) {
        try {
            faturaService.pagarFatura(request.cartaoId(), request.mes(), request.ano(), request.contaId());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/exportar/{cartaoId}/{mes}/{ano}")
    public ResponseEntity<byte[]> exportarFaturasParaPdf(
            @PathVariable UUID cartaoId,
            @PathVariable int mes,
            @PathVariable int ano) {

        try {

            DataValidador.validarMesEAno(mes, ano);

            byte[] pdfBytes = faturaService.exportarFaturasPorMes(cartaoId, mes, ano);


            String nomeMes = LocalDate.of(ano, mes, 1)
                    .getMonth()
                    .getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));

            String nomeArquivo = String.format("fatura-%s-%d-%d.pdf", nomeMes, mes, ano);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (DocumentException | IOException e) {
            throw new RelatorioException("Falha ao gerar relatório PDF", e);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage().getBytes());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
