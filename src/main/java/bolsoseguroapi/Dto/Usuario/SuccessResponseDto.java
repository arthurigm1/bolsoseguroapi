package bolsoseguroapi.Dto.Usuario;

import java.util.UUID;

public class SuccessResponseDto {
    private String nome;
    private String token;


    public SuccessResponseDto(String nome, String token) {
        this.nome = nome;
        this.token = token;

    }

    // Getters
    public String getNome() {
        return nome;
    }

    public String getToken() {
        return token;
    }


}
