package bolsoseguroapi.Service;

import bolsoseguroapi.Dto.Transacao.TransacaoDTO;
import bolsoseguroapi.Model.Conta;
import bolsoseguroapi.Model.Despesa;
import bolsoseguroapi.Model.Receita;
import bolsoseguroapi.Repository.ContaRepository;
import bolsoseguroapi.Repository.DespesaRepository;
import bolsoseguroapi.Repository.ReceitaRepository;
import bolsoseguroapi.Model.Usuario;
import bolsoseguroapi.Security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransacaoService {

    @Autowired
    private ReceitaRepository receitaRepository;

    @Autowired
    private DespesaRepository despesaRepository;

    @Autowired
    private SecurityService securityService;
@Autowired
private ContaRepository contaRepository;

    public List<TransacaoDTO> obterUltimasTransacoesDeTodasContas() {
        // Obter o usuário logado
        Usuario usuario = securityService.obterUsuarioLogado();
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        // Obter todas as contas do usuário
        List<Conta> contasDoUsuario = contaRepository.findByUsuario(usuario);

        // Buscar todas as receitas e despesas associadas às contas do usuário, ordenadas pela data de cadastro
        List<Receita> receitas = receitaRepository.findReceitasByContasOrderByDataCadastroDesc(contasDoUsuario);
        List<Despesa> despesas = despesaRepository.findDespesasByContasOrderByDataCadastroDesc(contasDoUsuario);

        // Combinar as duas listas em uma só
        List<Object> transacoes = new ArrayList<>();
        transacoes.addAll(receitas);
        transacoes.addAll(despesas);

        // Ordenar todas as transações pela data de cadastro (do mais recente para o mais antigo)
        transacoes.sort((t1, t2) -> {
            LocalDateTime dataCadastro1 = (t1 instanceof Receita) ? ((Receita) t1).getDataCadastro() : ((Despesa) t1).getDataCadastro();
            LocalDateTime dataCadastro2 = (t2 instanceof Receita) ? ((Receita) t2).getDataCadastro() : ((Despesa) t2).getDataCadastro();
            return dataCadastro2.compareTo(dataCadastro1);  // Do mais recente para o mais antigo
        });

        // Mapear as transações para o DTO
        return transacoes.stream()
                .limit(5) // Limitar a 5 transações
                .map(transacao -> {
                    if (transacao instanceof Receita) {
                        Receita receita = (Receita) transacao;
                        return new TransacaoDTO(
                                receita.getConta().getBanco(),
                                receita.getValor(),
                                "RECEITA",
                                receita.getData() // Usar dataCadastro
                        );
                    } else {
                        Despesa despesa = (Despesa) transacao;
                        return new TransacaoDTO(
                                despesa.getConta().getBanco(),
                                despesa.getValor(),
                                "DESPESA",
                                despesa.getData() // Usar dataCadastro
                        );
                    }
                })
                .collect(Collectors.toList());
    }
}
