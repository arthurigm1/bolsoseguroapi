package bolsoseguroapi.Controller;

import bolsoseguroapi.Dto.Usuario.LoginRequestDto;
import bolsoseguroapi.Dto.Usuario.RegisterRequestDto;
import bolsoseguroapi.Dto.Usuario.ResponseDto;
import bolsoseguroapi.Dto.Usuario.SuccessResponseDto;
import bolsoseguroapi.Exceptions.RegistroException;
import bolsoseguroapi.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid LoginRequestDto body) {
        Object response = authService.login(body);
        if (response instanceof SuccessResponseDto) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> register(@RequestBody @Valid RegisterRequestDto body) {
        try {
            ResponseDto response = authService.registerUser(body);
            return ResponseEntity.ok(response);
        } catch (RegistroException e) {
            return ResponseEntity.badRequest().body(new ResponseDto(e.getMessage(), null, null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseDto("Erro interno no servidor", null, null));
        }
    }


}
