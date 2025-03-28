package bolsoseguroapi.Dto.Cartao;

import bolsoseguroapi.Model.Enum.BandeiraCartao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CartaoDTO(String nome, BigDecimal limiteTotal, BandeiraCartao bandeira, int vencimentoFatura, int diaFechamentoFatura) {
}

