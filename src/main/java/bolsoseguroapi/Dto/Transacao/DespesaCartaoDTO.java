package bolsoseguroapi.Dto.Transacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record DespesaCartaoDTO (
        BigDecimal valor,
        LocalDate data,
        String categoria,
        String descricao
) {}