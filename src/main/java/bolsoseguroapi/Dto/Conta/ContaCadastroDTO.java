package bolsoseguroapi.Dto.Conta;

import bolsoseguroapi.Model.Enum.BancoTipo;
import bolsoseguroapi.Model.Usuario;

import java.math.BigDecimal;

public record ContaCadastroDTO(String banco, BigDecimal saldo, BancoTipo bancoTipo) {
}
