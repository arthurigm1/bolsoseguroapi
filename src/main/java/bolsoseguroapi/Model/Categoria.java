package bolsoseguroapi.Model;

import bolsoseguroapi.Model.Enum.TipoCategoria;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nome;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = true)
    private Usuario usuario;
    @Enumerated(EnumType.STRING)

    private TipoCategoria tipo;

    private boolean fixa;

    public Categoria(String nome, Boolean fixa, TipoCategoria tipo) {
        this.nome = nome;
        this.fixa = fixa;
        this.tipo = tipo;
    }


}