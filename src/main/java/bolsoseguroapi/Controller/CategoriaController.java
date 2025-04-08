package bolsoseguroapi.Controller;

import bolsoseguroapi.Dto.Categoria.CategoriaDTO;
import bolsoseguroapi.Model.Enum.TipoCategoria;
import bolsoseguroapi.Service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@RequiredArgsConstructor
@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaDTO> criarCategoriaPersonalizada(@RequestBody CategoriaDTO dto) {
        CategoriaDTO novaCategoria = categoriaService.criarCategoriaPersonalizada(dto.getNome(), dto.getTipo());
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
    }


    @GetMapping("")
    public ResponseEntity<List<CategoriaDTO>> getTodasCategorias() {
        List<CategoriaDTO> todasCategorias = List.of(
                categoriaService.getCategoriasFixas(),
                categoriaService.getCategoriasPersonalizadasDoUsuario()
        ).stream().flatMap(List::stream).toList();

        return ResponseEntity.ok(todasCategorias);
    }

    @GetMapping("/despesas")
    public ResponseEntity<List<CategoriaDTO>> getCategoriasDespesas() {
        return ResponseEntity.ok(categoriaService.getCategoriasPorTipo(TipoCategoria.DESPESA));
    }

    @GetMapping("/receitas")
    public ResponseEntity<List<CategoriaDTO>> getCategoriasReceitas() {
        return ResponseEntity.ok(categoriaService.getCategoriasPorTipo(TipoCategoria.RECEITA));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarCategoria(@PathVariable Long id) {
        try {
            categoriaService.deletarCategoria(id);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("erro", e.getMessage()));
        }
    }


}