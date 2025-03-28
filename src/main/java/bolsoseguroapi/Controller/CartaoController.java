package bolsoseguroapi.Controller;

import bolsoseguroapi.Dto.Cartao.CartaoDTO;
import bolsoseguroapi.Dto.Cartao.CartaoResponseDTO;
import bolsoseguroapi.Model.Cartao;
import bolsoseguroapi.Service.CartaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

}
