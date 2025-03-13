package bolsoseguroapi.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "metas_financeiras")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetaFinanceira {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;  // Cada meta pertence a um usuário

    @Column(nullable = false)
    private String nome;  // Ex: "Economizar para Viagem"

    @Column(nullable = false)
    private BigDecimal valorAtual = BigDecimal.ZERO;  // Começa com 0

    @Column(nullable = false)
    private BigDecimal valorMeta;  // Ex: R$ 5.000,00

    public boolean atingiuMeta() {
        return valorAtual.compareTo(valorMeta) >= 0;
    }
}