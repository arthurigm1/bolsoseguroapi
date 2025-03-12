package bolsoseguroapi.Validador;

import bolsoseguroapi.Dto.Conta.ContaCadastroDTO;
import bolsoseguroapi.Exceptions.ErroException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ContaValidador {



    public static void validar(ContaCadastroDTO contaDTO) {
        if (contaDTO == null) {
            throw new ErroException("O DTO da conta não pode ser nulo.");
        }
        validarBanco(contaDTO.banco());

    }



    private static void validarBanco(String banco) {
        if (banco == null || banco.isBlank()) {
            throw new ErroException("O nome do banco é obrigatório.");
        }
    }


}

