package bolsoseguroapi.Controller;

import bolsoseguroapi.Dto.Usuario.SaldoResponseDTO;
import bolsoseguroapi.Dto.Usuario.UsuarioInfoResponse;
import bolsoseguroapi.Service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UsuarioController {


    private final UsuarioService usuarioService;


    @GetMapping("")
    public ResponseEntity<SaldoResponseDTO> obterSaldo() {
        SaldoResponseDTO saldo = usuarioService.obterSaldo();
        return ResponseEntity.ok(saldo);
    }


    @GetMapping("/info")
    public ResponseEntity<UsuarioInfoResponse> getUsuario() {
        UsuarioInfoResponse usuarioInfoResponse = usuarioService.getUsuario();
        return ResponseEntity.ok(usuarioInfoResponse);
    }

}
