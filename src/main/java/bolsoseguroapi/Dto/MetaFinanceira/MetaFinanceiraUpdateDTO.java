package bolsoseguroapi.Dto.MetaFinanceira;

import java.math.BigDecimal;

public record MetaFinanceiraUpdateDTO(
        String nomeMeta,
        BigDecimal valorMeta,
                                      BigDecimal valorAtual) {
}
