package bolsoseguroapi.Validador;

import java.time.LocalDate;

public class DataValidador {

    public static void validarMesEAno(int mes, int ano) {
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("Mês inválido. Deve ser entre 1 e 12.");
        }
        int anoAtual = LocalDate.now().getYear();
        if (ano < 2020 || ano > anoAtual + 1) {
            throw new IllegalArgumentException("Ano inválido.");
        }
    }
}