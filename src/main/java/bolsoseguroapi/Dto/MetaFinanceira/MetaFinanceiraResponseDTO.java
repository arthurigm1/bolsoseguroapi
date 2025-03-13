package bolsoseguroapi.Dto.MetaFinanceira;


import java.math.BigDecimal;
import java.util.UUID;

public record MetaFinanceiraResponseDTO(
        UUID id,
        String nome,
        BigDecimal valorAtual,
        BigDecimal valorMeta,
        boolean atingida
) {}