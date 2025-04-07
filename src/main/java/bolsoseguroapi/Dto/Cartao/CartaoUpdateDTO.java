package bolsoseguroapi.Dto.Cartao;

import java.math.BigDecimal;

public record CartaoUpdateDTO(String nomeCartao,
        BigDecimal limiteTotal,
        BigDecimal limiteDisponivel) {
}
