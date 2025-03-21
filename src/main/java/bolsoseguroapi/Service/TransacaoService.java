package bolsoseguroapi.Service;

import bolsoseguroapi.Dto.Transacao.BalancoMensalDetalhadoDTO;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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


    public BigDecimal obterTotalDespesasMes() {
        Usuario usuario = securityService.obterUsuarioLogado();
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado");
        }


        List<Conta> contasDoUsuario = contaRepository.findByUsuario(usuario);
        YearMonth mesAtual = YearMonth.now();
        LocalDate startDate = mesAtual.atDay(1);
        LocalDate today = LocalDate.now();

        // Calcular despesas do usuário
        return despesaRepository.calcularTotalDespesasMensalPorContas(contasDoUsuario, startDate, today);
    }

    public BigDecimal  obterTotalReceitasMes(){
        Usuario usuario = securityService.obterUsuarioLogado();
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado");
        }


        List<Conta> contasDoUsuario = contaRepository.findByUsuario(usuario);
        YearMonth mesAtual = YearMonth.now();
        LocalDate startDate = mesAtual.atDay(1);
        LocalDate today = LocalDate.now();


        return receitaRepository.calcularTotalReceitasMensalPorContas(contasDoUsuario, startDate, today);
    }

    public List<BalancoMensalDetalhadoDTO> obterBalancoUltimosMeses() {
        int mesAtual = LocalDate.now().getMonthValue();
        int mesesConsiderados = Math.min(mesAtual, 6);

        List<BalancoMensalDetalhadoDTO> balancoMensal = new ArrayList<>();

        for (int i = mesesConsiderados - 1; i >= 0; i--) {
            YearMonth mes = YearMonth.now().minusMonths(i);
            LocalDate dataInicial = mes.atDay(1);
            LocalDate dataFinal = mes.atEndOfMonth();

            BigDecimal totalReceitas = receitaRepository.calcularTotalReceitasMensal(dataInicial, dataFinal);
            BigDecimal totalDespesas = despesaRepository.calcularTotalDespesasMensal(dataInicial, dataFinal);

            String nomeMes = mes.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));

            balancoMensal.add(new BalancoMensalDetalhadoDTO(nomeMes, totalDespesas, totalReceitas));
        }

        return balancoMensal;
    }

    public BigDecimal calcularSaldoCategoria() {
        Usuario usuario = securityService.obterUsuarioLogado(); // Obter o usuário logado
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        // Buscar as receitas do usuário logado pela conta e categoria (usando o ID da categoria)
        List<Receita> receitas = receitaRepository.findByContaUsuarioAndCategoriaId(usuario, 13L);

        // Somar os valores das receitas com BigDecimal para evitar problemas de precisão
        return receitas.stream()
                .map(Receita::getValor)  // Extrai o valor de cada receita
                .filter(valor -> valor != null)  // Filtra valores nulos, caso haja
                .reduce(BigDecimal.ZERO, BigDecimal::add);  // Soma os valores
    }
}
