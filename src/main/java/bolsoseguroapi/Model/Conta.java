package bolsoseguroapi.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "contas_bancarias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 50)
    private String banco;


    @Column(nullable = false)
    private BigDecimal saldo = BigDecimal.ZERO;

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Despesa> despesas;

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Receita> receitas;


}
