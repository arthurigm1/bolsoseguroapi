package bolsoseguroapi.Dto.Conta;

import bolsoseguroapi.Model.Usuario;

import java.math.BigDecimal;

public record ContaCadastroDTO(Usuario usuario, String banco, BigDecimal saldo) {
}
