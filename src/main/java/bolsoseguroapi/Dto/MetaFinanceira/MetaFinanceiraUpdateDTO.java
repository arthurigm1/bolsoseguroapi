package bolsoseguroapi.Dto.MetaFinanceira;

import java.math.BigDecimal;

public record MetaFinanceiraUpdateDTO(BigDecimal valorMeta,
                                      BigDecimal valorAtual) {
}
