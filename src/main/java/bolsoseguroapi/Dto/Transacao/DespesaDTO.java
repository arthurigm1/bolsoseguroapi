package bolsoseguroapi.Dto.Transacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record DespesaDTO(
        BigDecimal valor,
        LocalDate data,
        String categoria,
        String descricao,
        UUID contaId
) {}
