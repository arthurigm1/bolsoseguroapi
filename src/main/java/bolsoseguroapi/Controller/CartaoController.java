package bolsoseguroapi.Controller;

import bolsoseguroapi.Dto.Cartao.CartaoDTO;
import bolsoseguroapi.Dto.Cartao.CartaoResponseDTO;
import bolsoseguroapi.Dto.Cartao.CartaoUpdateDTO;
import bolsoseguroapi.Model.Cartao;
import bolsoseguroapi.Service.CartaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cartoes")
@RequiredArgsConstructor
public class CartaoController {

    private final CartaoService cartaoService;

    @PostMapping()
    public ResponseEntity<Cartao> criarCartao(@RequestBody CartaoDTO cartaoDTO) {
        Cartao cartao = cartaoService.criarCartao(cartaoDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<List<CartaoResponseDTO>> buscarCartoesPorUsuario() {
        List<CartaoResponseDTO> cartoes = cartaoService.buscarCartoesPorUsuario();
        return ResponseEntity.ok(cartoes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCartao(@PathVariable UUID id) {
        cartaoService.deleteCartao(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> updateCartao(@PathVariable UUID id, @RequestBody CartaoUpdateDTO dto) {
        cartaoService.updateCartao(id, dto);
        return ResponseEntity.noContent().build();
    }


}
