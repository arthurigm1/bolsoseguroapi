package bolsoseguroapi.Controller;

import bolsoseguroapi.Dto.MetaFinanceira.MetaFinanceiraRequestDTO;
import bolsoseguroapi.Dto.MetaFinanceira.MetaFinanceiraResponseDTO;
import bolsoseguroapi.Dto.MetaFinanceira.MetaFinanceiraUpdateDTO;
import bolsoseguroapi.Service.MetaFinanceiraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/metas")
public class MetaFinanceiraController {
    private final MetaFinanceiraService metaService;


    @PostMapping
    public ResponseEntity<MetaFinanceiraResponseDTO> criarMeta(@RequestBody   @Valid MetaFinanceiraRequestDTO dto) {
        return ResponseEntity.ok(metaService.criarMeta(dto));
    }

    @GetMapping
    public ResponseEntity<List<MetaFinanceiraResponseDTO>> listarMetas() {
        return ResponseEntity.ok(metaService.listarMetasUsuario());
    }


    @PutMapping("/{metaId}")
    public ResponseEntity<MetaFinanceiraUpdateDTO> editarMeta(
            @PathVariable UUID metaId,
            @RequestBody MetaFinanceiraUpdateDTO updateDTO) {
        metaService.editarMeta(metaId, updateDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{metaId}")
    public ResponseEntity<Void> deletarMeta(@PathVariable UUID metaId) {
        metaService.deletarMeta(metaId);
        return ResponseEntity.noContent().build();
    }


}
