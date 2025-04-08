package bolsoseguroapi.Dto.Cartao;

import bolsoseguroapi.Model.Enum.BandeiraCartao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CartaoDTO(    @NotBlank
                            String nome,

                            @NotNull
                            @Positive
                            BigDecimal limiteTotal,

                            @NotNull
                            BandeiraCartao bandeira,

                            @Positive
                            int vencimentoFatura,

                            @Positive
                            int diaFechamentoFatura) {
}

