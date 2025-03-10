package bolsoseguroapi.Dto.Usuario;

import java.util.UUID;

public record ResponseDto (String nome, String token, UUID id) { }
