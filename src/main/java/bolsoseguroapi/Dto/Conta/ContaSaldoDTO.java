package bolsoseguroapi.Dto.Conta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContaSaldoDTO {
    private UUID id;
    private String nome;
    private BigDecimal saldo;

}
