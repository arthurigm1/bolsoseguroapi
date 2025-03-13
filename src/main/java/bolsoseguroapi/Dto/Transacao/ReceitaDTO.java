package bolsoseguroapi.Dto.Transacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReceitaDTO(
        BigDecimal valor,
        LocalDate date,
        String  categoria,
        String descricao,
        UUID contaId
) {}