package bolsoseguroapi.Service;

import bolsoseguroapi.Dto.Cartao.CartaoDTO;
import bolsoseguroapi.Dto.Cartao.CartaoResponseDTO;
import bolsoseguroapi.Dto.Cartao.CartaoUpdateDTO;
import bolsoseguroapi.Dto.Cartao.PagamentoFaturaDTO;
import bolsoseguroapi.Mapper.CartaoMapper;
import bolsoseguroapi.Model.Cartao;
import bolsoseguroapi.Model.Conta;
import bolsoseguroapi.Model.Usuario;
import bolsoseguroapi.Repository.*;
import bolsoseguroapi.Security.SecurityService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartaoService {

    private final CartaoRepository cartaoRepository;
    private final ContaRepository contaRepository;
    private final CategoriaRepository categoriaRepository;
    private final SecurityService securityService;
    private final UsuarioRepository usuarioRepository;



    public CartaoResponseDTO criarCartao(CartaoDTO cartaoDTO) {
        Usuario usuario = securityService.obterUsuarioLogado();

        Cartao cartao = new Cartao();
        cartao.setNome(cartaoDTO.nome());
        cartao.setLimiteTotal(cartaoDTO.limiteTotal());
        cartao.setLimiteDisponivel(cartaoDTO.limiteTotal());
        cartao.setBandeira(cartaoDTO.bandeira());
        cartao.setVencimentoFatura(cartaoDTO.vencimentoFatura());
        cartao.setDiaFechamentoFatura(cartaoDTO.diaFechamentoFatura());
        cartao.setUsuario(usuario);
        Cartao cartaoSalvo = cartaoRepository.save(cartao);

        return CartaoMapper.toDto(cartaoSalvo);
    }


    public List<CartaoResponseDTO> buscarCartoesPorUsuario() {
        Usuario usuario = securityService.obterUsuarioLogado();
        List<Cartao> cartoes = cartaoRepository.findByUsuarioId(usuario.getId());
        return cartoes.stream()
                .map(CartaoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteCartao(UUID id) {
        Cartao cartao = cartaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));
        cartao.getDespesas().clear();
        cartao.getFaturas().clear();

        cartaoRepository.delete(cartao);
    }

    public void updateCartao(UUID id, CartaoUpdateDTO cartaoUpdateDTO) {
        Cartao cartao = cartaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));

        if (cartaoUpdateDTO.nomeCartao() != null) {
            cartao.setNome(cartaoUpdateDTO.nomeCartao());
        }
        if (cartaoUpdateDTO.limiteTotal() != null) {
            cartao.setLimiteTotal(cartaoUpdateDTO.limiteTotal());
        }
        if (cartaoUpdateDTO.limiteDisponivel() != null) {
            cartao.setLimiteDisponivel(cartaoUpdateDTO.limiteDisponivel());
        }

        cartaoRepository.save(cartao);
    }



}
