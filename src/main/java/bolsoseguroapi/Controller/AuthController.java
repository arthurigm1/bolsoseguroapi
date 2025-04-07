package bolsoseguroapi.Controller;

import bolsoseguroapi.Dto.Usuario.*;
import bolsoseguroapi.Exceptions.RegistroException;
import bolsoseguroapi.Model.PasswordResetToken;
import bolsoseguroapi.Model.Usuario;
import bolsoseguroapi.Service.AuthService;
import bolsoseguroapi.Service.PasswordService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final PasswordService passwordService;
    private final AuthService authService;

    @GetMapping("/verify")
    public String verifyUser(@RequestParam("code") String code) {
        return authService.verify(code) ? "verify_success" : "verify_fail";
    }

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

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("password");
        String message = passwordService.resetPassword(token, newPassword);
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String message = passwordService.forgotPassword(email);

            Map<String, String> response = new HashMap<>();
            response.put("message", message);

            return ResponseEntity.ok(response);
        } catch (RuntimeException | MessagingException | UnsupportedEncodingException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/changepass")
    public ResponseEntity<String> alterarSenha(@RequestBody AlterarSenhadto alterarSenhaDTO) {
        boolean sucesso = authService.alterarSenha(alterarSenhaDTO);
        return sucesso ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
