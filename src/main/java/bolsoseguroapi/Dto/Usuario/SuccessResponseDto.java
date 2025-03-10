package bolsoseguroapi.Dto.Usuario;

import java.util.UUID;

public class SuccessResponseDto {
    private String nome;
    private String token;
    private UUID id;

    public SuccessResponseDto(String nome, String token, UUID id) {
        this.nome = nome;
        this.token = token;
        this.id = id;
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public String getToken() {
        return token;
    }

    public UUID getId() {
        return id;
    }
}
