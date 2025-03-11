package bolsoseguroapi.Dto.Categoria;

public class CategoriaDTO {
    private Long id;
    private String nome;
    private boolean fixa;  // Se for fixa ou personalizada

    public CategoriaDTO(Long id, String nome, boolean fixa) {
        this.id = id;
        this.nome = nome;
        this.fixa = fixa;
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
}

