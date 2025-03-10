package bolsoseguroapi.Dto.Usuario;

public class ErrorResponseDto {
    private String message;

    public ErrorResponseDto(String message) {
        this.message = message;
    }

    // Getter
    public String getMessage() {
        return message;
    }
}
