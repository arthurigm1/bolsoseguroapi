package bolsoseguroapi.Dto.Cartao;

import bolsoseguroapi.Model.Enum.BandeiraCartao;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CartaoDTO(@NotBlank String nome,
                        @NotBlank BigDecimal limiteTotal,
                        @NotBlank BandeiraCartao bandeira,
                        @NotBlank int vencimentoFatura,
                        @NotBlank int diaFechamentoFatura) {
}

