package bolsoseguroapi.Controller;

import bolsoseguroapi.Dto.Categoria.CategoriaDTO;
import bolsoseguroapi.Model.Enum.TipoCategoria;
import bolsoseguroapi.Service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {
    @Autowired
    private CategoriaService categoriaService;

    @PostMapping("")
    public ResponseEntity<String> criarCategoriaPersonalizada(@RequestBody CategoriaDTO categoriaDTO) {
        categoriaService.criarCategoriaPersonalizada(categoriaDTO.getNome(), categoriaDTO.getTipo());
        return ResponseEntity.ok().build();
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
    public ResponseEntity<Void> deletarConta(@PathVariable Long id) throws AccessDeniedException {
        categoriaService.deletarConta(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}