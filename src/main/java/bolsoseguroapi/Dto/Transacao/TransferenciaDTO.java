package bolsoseguroapi.Dto.Transacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransferenciaDTO(
        BigDecimal valor,
        LocalDate data,
        String descricao,
        UUID contaOrigemId,
        UUID contaDestinoId
) {}
