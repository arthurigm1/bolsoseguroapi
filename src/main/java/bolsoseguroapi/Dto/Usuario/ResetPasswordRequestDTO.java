package bolsoseguroapi.Dto.Usuario;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequestDTO(@NotBlank String token, @NotBlank String password) {
}
