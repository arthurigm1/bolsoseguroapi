package bolsoseguroapi.Dto.MetaFinanceira;


import java.math.BigDecimal;

public record MetaFinanceiraRequestDTO(
        String nome,
        BigDecimal valorMeta,
        BigDecimal valorAtual
) {}