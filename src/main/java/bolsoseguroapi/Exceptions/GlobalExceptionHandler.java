package bolsoseguroapi.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErroResposta handleErrosNaoTratados(RuntimeException e){
        return new ErroResposta(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Ocorreu um erro interno.",List.of());
    }

    @ExceptionHandler(CategoriaLimitException.class)
    public ResponseEntity<String> handleCategoriaLimitException(CategoriaLimitException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Você já atingiu o limite de categorias personalizadas (5).");
    }

    @ExceptionHandler(ErroException.class)
    public ResponseEntity<String> handleErroException(ErroException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}
