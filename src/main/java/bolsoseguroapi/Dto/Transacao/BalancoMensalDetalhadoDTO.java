package bolsoseguroapi.Dto.Transacao;

import java.math.BigDecimal;

public record BalancoMensalDetalhadoDTO(String mes,
                                         BigDecimal despesa,
                                         BigDecimal receita) {
}
