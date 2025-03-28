package bolsoseguroapi.Dto.Cartao;

import bolsoseguroapi.Model.Enum.BandeiraCartao;

import java.math.BigDecimal;
import java.util.UUID;

public record CartaoResponseDTO(UUID id,String nome, BigDecimal limiteDisponivel , BandeiraCartao bandeira) {
}
