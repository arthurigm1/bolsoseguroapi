package bolsoseguroapi.Dto.Usuario;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDto(@NotBlank String nome,
                                 @NotBlank String email,
                                 @NotBlank String senha)

{}