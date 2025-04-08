package bolsoseguroapi.Dto.Conta;

import jakarta.persistence.Column;

import java.math.BigDecimal;

public record ContaUpdateDTO( String banco, BigDecimal saldo) {
}
