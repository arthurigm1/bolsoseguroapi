package bolsoseguroapi.Dto.Cartao;

import java.math.BigDecimal;
import java.util.UUID;

public record PagamentoFaturaDTO( UUID contaId,
         Long cartaoId,
         BigDecimal valor,
         boolean pagamentoTotal,
         String descricao) {
}
