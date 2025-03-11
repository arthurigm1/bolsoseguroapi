package bolsoseguroapi.Controller;

import bolsoseguroapi.Dto.Categoria.CategoriaDTO;
import bolsoseguroapi.Service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {
    @Autowired
    private CategoriaService categoriaService;

    @PostMapping("")
    public ResponseEntity<String> criarCategoriaPersonalizada(
            @RequestBody Map<String, String> body) {
        String nome = body.get("nome");
        categoriaService.criarCategoriaPersonalizada(nome);
        return ResponseEntity.ok("Categoria personalizada criada com sucesso.");
    }

    @GetMapping("")
    public ResponseEntity<List<CategoriaDTO>> getTodasCategorias() {

        List<CategoriaDTO> categoriasFixas = categoriaService.getCategoriasFixas();
        List<CategoriaDTO> categoriasPersonalizadas = categoriaService.getCategoriasPersonalizadasDoUsuario();
        List<CategoriaDTO> todasCategorias = new ArrayList<>();
        todasCategorias.addAll(categoriasFixas);
        todasCategorias.addAll(categoriasPersonalizadas);

        return ResponseEntity.ok(todasCategorias);
    }

}