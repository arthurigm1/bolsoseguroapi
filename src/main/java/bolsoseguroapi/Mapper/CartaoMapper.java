package bolsoseguroapi.Mapper;

import bolsoseguroapi.Dto.Cartao.CartaoResponseDTO;
import bolsoseguroapi.Model.Cartao;

public class CartaoMapper {
    public static CartaoResponseDTO toDto(Cartao cartao) {
        return new CartaoResponseDTO(
                cartao.getId(),
                cartao.getNome(),
                cartao.getLimiteDisponivel(),
                cartao.getBandeira()

        );
    }

}