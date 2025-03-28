package bolsoseguroapi.Service;

import bolsoseguroapi.Dto.Cartao.CartaoDTO;
import bolsoseguroapi.Dto.Cartao.CartaoResponseDTO;
import bolsoseguroapi.Dto.Cartao.PagamentoFaturaDTO;
import bolsoseguroapi.Model.Cartao;
import bolsoseguroapi.Model.Conta;
import bolsoseguroapi.Model.Usuario;
import bolsoseguroapi.Repository.CartaoRepository;
import bolsoseguroapi.Repository.CategoriaRepository;
import bolsoseguroapi.Repository.ContaRepository;
import bolsoseguroapi.Repository.UsuarioRepository;
import bolsoseguroapi.Security.SecurityService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartaoService {

    private final CartaoRepository cartaoRepository;
    private final ContaRepository contaRepository;
    private final CategoriaRepository categoriaRepository;
    private final SecurityService securityService;
    private final UsuarioRepository usuarioRepository;

    public Cartao criarCartao(CartaoDTO cartaoDTO) {
        Usuario usuario = securityService.obterUsuarioLogado();

        usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        // Criar o novo cartão
        Cartao cartao = new Cartao();
        cartao.setNome(cartaoDTO.nome());
        cartao.setLimiteTotal(cartaoDTO.limiteTotal());
        cartao.setLimiteDisponivel(cartaoDTO.limiteTotal());  // Limite disponível é o mesmo que o total no momento da criação
        cartao.setBandeira(cartaoDTO.bandeira());
        cartao.setVencimentoFatura(cartaoDTO.vencimentoFatura());
        cartao.setUsuario(usuario);

        // Atribuir um valor para 'diaFechamentoFatura' (necessário, se você tiver esse campo na sua lógica)
        cartao.setDiaFechamentoFatura(cartaoDTO.diaFechamentoFatura());  // Por exemplo, fecha no dia 30 de cada mês (ajuste conforme a lógica)

        // Salva o cartão no repositório
        return cartaoRepository.save(cartao);
    }


    public List<CartaoResponseDTO> buscarCartoesPorUsuario() {
        Usuario usuario = securityService.obterUsuarioLogado();
        List<Cartao> cartoes = cartaoRepository.findByUsuarioId(usuario.getId());
        return cartoes.stream()
                .map(cartao -> new CartaoResponseDTO(
                        cartao.getId(),
                        cartao.getNome(),
                        cartao.getLimiteDisponivel(),
                        cartao.getBandeira()
                ))
                .collect(Collectors.toList());
    }


}
