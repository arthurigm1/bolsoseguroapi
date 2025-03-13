package bolsoseguroapi.Service;


import bolsoseguroapi.Dto.Transacao.DespesaDTO;
import bolsoseguroapi.Model.Categoria;
import bolsoseguroapi.Model.Conta;
import bolsoseguroapi.Model.Despesa;
import bolsoseguroapi.Model.Usuario;
import bolsoseguroapi.Repository.CategoriaRepository;
import bolsoseguroapi.Repository.ContaRepository;
import bolsoseguroapi.Repository.DespesaRepository;
import bolsoseguroapi.Repository.UsuarioRepository;
import bolsoseguroapi.Security.SecurityService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class DespesaService {
    @Autowired
    private DespesaRepository despesaRepository;
    @Autowired
    private ContaRepository contaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private SecurityService securityService;

    public Despesa adicionarDespesa(DespesaDTO despesaDTO) {
        Usuario usuario = securityService.obterUsuarioLogado();
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        Conta conta = contaRepository.findById(despesaDTO.contaId())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        Categoria categoria = categoriaRepository.findById(Long.parseLong(despesaDTO.categoria()))
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        Despesa despesa = new Despesa();
        despesa.setConta(conta);
        despesa.setCategoria(categoria);
        despesa.setValor(despesaDTO.valor());
        despesa.setData(despesaDTO.data());
        despesa.setDescricao(despesaDTO.descricao());

        // Atualiza o saldo da conta
        conta.setSaldo(conta.getSaldo().subtract(despesaDTO.valor()));
        contaRepository.save(conta);

        // Atualiza o saldoGeral manualmente após a alteração das contas
        usuario.setSaldoGeral(usuario.getContas().stream()
                .map(Conta::getSaldo)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        usuarioRepository.save(usuario);

        return despesaRepository.save(despesa);
    }
}