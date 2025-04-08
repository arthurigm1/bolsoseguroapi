package bolsoseguroapi.Dto.MetaFinanceira;


import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record MetaFinanceiraRequestDTO(
        @NotBlank
        String nome,
        @NotBlank
        BigDecimal valorMeta,
        @NotBlank
        BigDecimal valorAtual
) {}