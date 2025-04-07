package bolsoseguroapi.Model;

import bolsoseguroapi.Model.Enum.BandeiraCartao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cartoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cartao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "cartao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Despesa> despesas = new ArrayList<>();

    @OneToMany(mappedBy = "cartao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FaturaCartao> faturas = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 50)
    private String nome;

    private BigDecimal limiteDisponivel;

    @Column(nullable = false)
    private BigDecimal limiteTotal;

    private BigDecimal faturaAtual = BigDecimal.ZERO;


    @Enumerated(EnumType.STRING)
    private BandeiraCartao bandeira;


    @Column(nullable = false)
    private int vencimentoFatura;

    @Column(nullable = false)
    private int diaFechamentoFatura;
}
