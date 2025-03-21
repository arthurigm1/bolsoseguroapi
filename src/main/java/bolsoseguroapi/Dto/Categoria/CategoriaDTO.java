package bolsoseguroapi.Dto.Categoria;

import bolsoseguroapi.Model.Enum.TipoCategoria;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CategoriaDTO {
    private Long id;
    private String nome;

    @JsonProperty("fixa")  // Mantém o nome correto no JSON
    private boolean fixa;

    private TipoCategoria tipo;  // RECEITA ou DESPESA

    // Construtor sem argumentos para serialização JSON
    public CategoriaDTO() {}

    // Construtor com todos os campos
    public CategoriaDTO(Long id, String nome, boolean fixa, TipoCategoria tipo) {
        this.id = id;
        this.nome = nome;
        this.fixa = fixa;
        this.tipo = tipo;
    }



    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isFixa() {
        return fixa;
    }

    public void setFixa(boolean fixa) {
        this.fixa = fixa;
    }

    public TipoCategoria getTipo() {
        return tipo;
    }

    public void setTipo(TipoCategoria tipo) {
        this.tipo = tipo;
    }
}
