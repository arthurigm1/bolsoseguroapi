package bolsoseguroapi.Controller;

import bolsoseguroapi.Dto.MetaFinanceira.MetaFinanceiraRequestDTO;
import bolsoseguroapi.Dto.MetaFinanceira.MetaFinanceiraResponseDTO;
import bolsoseguroapi.Dto.MetaFinanceira.MetaFinanceiraUpdateDTO;
import bolsoseguroapi.Service.MetaFinanceiraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/metas")
public class MetaFinanceiraController {
    private final MetaFinanceiraService metaService;

    public MetaFinanceiraController(MetaFinanceiraService metaService) {
        this.metaService = metaService;
    }

    // Criar uma nova meta financeira
    @PostMapping
    public ResponseEntity<MetaFinanceiraResponseDTO> criarMeta(@RequestBody MetaFinanceiraRequestDTO dto) {
        return ResponseEntity.ok(metaService.criarMeta(dto));
    }

    // Listar todas as metas do usuário logado
    @GetMapping
    public ResponseEntity<List<MetaFinanceiraResponseDTO>> listarMetas() {
        return ResponseEntity.ok(metaService.listarMetasUsuario());
    }


    @PutMapping("/{metaId}")
    public ResponseEntity<MetaFinanceiraResponseDTO> editarMeta(
            @PathVariable UUID metaId,
            @RequestBody MetaFinanceiraUpdateDTO updateDTO) {
        return ResponseEntity.ok(metaService.editarMeta(metaId, updateDTO));
    }

    @DeleteMapping("/{metaId}")
    public ResponseEntity<Void> deletarMeta(@PathVariable UUID metaId) {
        metaService.deletarMeta(metaId);
        return ResponseEntity.noContent().build();
    }


}
