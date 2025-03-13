package bolsoseguroapi.Controller;

import bolsoseguroapi.Dto.MetaFinanceira.MetaFinanceiraRequestDTO;
import bolsoseguroapi.Dto.MetaFinanceira.MetaFinanceiraResponseDTO;
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

    // Adicionar valor à meta
    @PutMapping("/{metaId}/adicionar")
    public ResponseEntity<MetaFinanceiraResponseDTO> adicionarValor(@PathVariable UUID metaId, @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(metaService.adicionarValor(metaId, valor));
    }

    // Verificar se a meta foi atingida
    @GetMapping("/{metaId}/verificar")
    public ResponseEntity<Boolean> verificarMeta(@PathVariable UUID metaId) {
        return ResponseEntity.ok(metaService.verificarMetaAtingida(metaId));
    }
}
