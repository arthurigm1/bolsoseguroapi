package bolsoseguroapi.Dto.Usuario;

import jakarta.validation.constraints.NotBlank;

public record AlterarSenhadto(@NotBlank String senhaAtual,
                              @NotBlank String novaSenha) {
}
