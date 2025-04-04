package bolsoseguroapi.Dto.Cartao;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PagamentoFaturaRequest(@NotNull UUID cartaoId,
                                     @Min(1) @Max(12) int mes,
                                     @Min(2000) int ano,
                                     @NotNull UUID contaId) {
}
