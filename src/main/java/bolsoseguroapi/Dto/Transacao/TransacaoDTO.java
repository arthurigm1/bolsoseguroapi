package bolsoseguroapi.Dto.Transacao;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransacaoDTO {
    private String nomeConta;
    private BigDecimal valor;
    private String tipoTransacao; // "RECEITA" ou "DESPESA"
    private LocalDate dataTransacao;

}