package bolsoseguroapi.Dto.Cartao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record DespesaCartaoDTO(
        UUID id,
        String descricao,
        BigDecimal valor,
        LocalDate data,
        String categoria)
{
}
