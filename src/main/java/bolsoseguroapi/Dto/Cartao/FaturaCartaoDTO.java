package bolsoseguroapi.Dto.Cartao;

import bolsoseguroapi.Model.Cartao;
import bolsoseguroapi.Model.Enum.BandeiraCartao;
import bolsoseguroapi.Model.FaturaCartao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
public record FaturaCartaoDTO(
        UUID id,
        UUID cartaoId,
        String nomeCartao,
        BandeiraCartao bandeira,
        BigDecimal limiteTotal,
        BigDecimal limiteDisponivel,
        LocalDate dataVencimento,
        BigDecimal valor,
        boolean paga,
        boolean reaberta,
        LocalDate dataReabertura,
        BigDecimal totalpago
) {
    public static FaturaCartaoDTO fromEntity(FaturaCartao fatura) {
        Cartao cartao = fatura.getCartao();
        return new FaturaCartaoDTO(
                fatura.getId(),
                cartao.getId(),
                cartao.getNome(),
                cartao.getBandeira(),
                cartao.getLimiteTotal(),
                cartao.getLimiteDisponivel(),
                fatura.getDataVencimento(),
                fatura.getValor(),
                fatura.isPaga(),
                fatura.isReaberta(),
                fatura.getDataReabertura(),
                fatura.getTotalpago()
        );
    }
}
