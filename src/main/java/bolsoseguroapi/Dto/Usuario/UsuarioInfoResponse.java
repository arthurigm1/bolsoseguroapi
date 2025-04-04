package bolsoseguroapi.Dto.Usuario;

import java.math.BigDecimal;

public record UsuarioInfoResponse(
        String nome,
        String email,
        boolean enabled,
        BigDecimal saldoGeral
) {
}
