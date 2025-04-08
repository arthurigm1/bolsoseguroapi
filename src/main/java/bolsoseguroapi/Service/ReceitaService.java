package bolsoseguroapi.Service;

import bolsoseguroapi.Dto.Transacao.ReceitaDTO;
import bolsoseguroapi.Model.Categoria;
import bolsoseguroapi.Model.Conta;
import bolsoseguroapi.Model.Receita;
import bolsoseguroapi.Model.Usuario;
import bolsoseguroapi.Repository.CategoriaRepository;
import bolsoseguroapi.Repository.ContaRepository;
import bolsoseguroapi.Repository.ReceitaRepository;
import bolsoseguroapi.Repository.UsuarioRepository;
import bolsoseguroapi.Security.SecurityService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ReceitaService {

    private final  ReceitaRepository receitaRepository;

    private final  ContaRepository contaRepository;

    private final  UsuarioRepository usuarioRepository;

    private final CategoriaRepository categoriaRepository;

    private final SecurityService securityService;


    public Receita adicionarReceita(ReceitaDTO receitaDTO) {
        Usuario usuario = securityService.obterUsuarioLogado();
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        Conta conta = contaRepository.findById(receitaDTO.contaId())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        Categoria categoria = categoriaRepository.findById(Long.parseLong(receitaDTO.categoria()))
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        Receita receita = new Receita();
        receita.setConta(conta);
        receita.setCategoria(categoria);
        receita.setValor(receitaDTO.valor());
        receita.setData(receitaDTO.data());
        receita.setDescricao(receitaDTO.descricao());

        // Atualiza o saldo da conta
        conta.setSaldo(conta.getSaldo().add(receitaDTO.valor()));
        contaRepository.save(conta);

        // Atualiza o saldoGeral manualmente após a alteração das contas
        usuario.setSaldoGeral(usuario.getContas().stream()
                .map(Conta::getSaldo)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        usuarioRepository.save(usuario);

        return receitaRepository.save(receita);
    }

}
