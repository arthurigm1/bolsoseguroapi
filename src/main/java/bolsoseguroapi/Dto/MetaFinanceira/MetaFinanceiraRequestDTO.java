package bolsoseguroapi.Dto.MetaFinanceira;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MetaFinanceiraRequestDTO(
        @NotBlank
        String nome,
        @NotNull
        BigDecimal valorMeta,
        @NotNull
        BigDecimal valorAtual
) {}