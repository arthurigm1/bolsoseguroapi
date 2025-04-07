package bolsoseguroapi.Service;


import bolsoseguroapi.Dto.Transacao.DespesaCartaoDTO;
import bolsoseguroapi.Dto.Transacao.DespesaDTO;
import bolsoseguroapi.Model.*;
import bolsoseguroapi.Repository.*;
import bolsoseguroapi.Security.SecurityService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private CartaoRepository cartaoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private FaturaRepository faturaRepository;

    public Despesa adicionarDespesa(DespesaDTO despesaDTO) {
        Usuario usuario = securityService.obterUsuarioLogado();
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        Categoria categoria = categoriaRepository.findById(Long.parseLong(despesaDTO.categoria()))
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        Despesa despesa = new Despesa();
        despesa.setCategoria(categoria);
        despesa.setValor(despesaDTO.valor());
        despesa.setData(despesaDTO.data());
        despesa.setDescricao(despesaDTO.descricao());

        if (despesaDTO.contaId() != null && despesaDTO.cartaoId() != null) {
            throw new RuntimeException("A despesa deve estar vinculada a uma conta OU a um cartão, não ambos.");
        }

        if (despesaDTO.contaId() != null) {
            registrarDespesaConta(despesa, usuario, despesaDTO.contaId());
        } else if (despesaDTO.cartaoId() != null) {
            registrarDespesaCartao(despesa, usuario, despesaDTO.cartaoId(), despesaDTO.data());
        } else {
            throw new RuntimeException("É necessário informar um ID de conta ou cartão.");
        }

        usuarioRepository.save(usuario);
        return despesaRepository.save(despesa);
    }
    private void registrarDespesaConta(Despesa despesa, Usuario usuario, UUID contaId) {
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        despesa.setConta(conta);

        // Atualiza saldo da conta
        conta.setSaldo(conta.getSaldo().subtract(despesa.getValor()));

        // Atualiza saldo geral do usuário
        usuario.setSaldoGeral(usuario.getContas().stream()
                .map(Conta::getSaldo)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        contaRepository.save(conta);
    }
    private void registrarDespesaCartao(Despesa despesa, Usuario usuario, UUID cartaoId, LocalDate dataDespesa) {
        Cartao cartao = cartaoRepository.findById(cartaoId)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));
        despesa.setCartao(cartao);

        // Calcula a fatura correta
        LocalDate dataFatura = calcularDataFatura(cartao, dataDespesa);

        Optional<FaturaCartao> faturaExistente = faturaRepository.findByCartaoAndDataVencimento(cartao, dataFatura);

        FaturaCartao fatura;

        if (faturaExistente.isPresent()) {
            fatura = faturaExistente.get();
            if (fatura.isPaga()) {
                fatura.setPaga(false);
                fatura.setDataPagamento(null);
                fatura.setReaberta(true);
                fatura.setDataReabertura(LocalDate.now());
            }
        } else {

            fatura = new FaturaCartao();
            fatura.setCartao(cartao);
            fatura.setDataVencimento(dataFatura);
            fatura.setValor(BigDecimal.ZERO);
            fatura.setPaga(false);
        }

        despesa.setFatura(fatura);
        fatura.setValor(fatura.getValor().add(despesa.getValor()));

        // Se for a fatura atual, reduz o limite do cartão
        if (dataFatura.getMonthValue() == LocalDate.now().getMonthValue() &&
                dataFatura.getYear() == LocalDate.now().getYear()) {
            cartao.setLimiteDisponivel(cartao.getLimiteDisponivel().subtract(despesa.getValor()));
        }

        // Salva as alterações
        faturaRepository.save(fatura);
        cartaoRepository.save(cartao);
    }



    private LocalDate calcularDataFatura(Cartao cartao, LocalDate dataDespesa) {
        int diaFechamento = cartao.getDiaFechamentoFatura();
        YearMonth anoMes;

        if (dataDespesa.getDayOfMonth() <= diaFechamento) {
            // A fatura será do mês atual
            anoMes = YearMonth.of(dataDespesa.getYear(), dataDespesa.getMonth());
        } else {
            // A fatura será do próximo mês
            LocalDate proximoMes = dataDespesa.plusMonths(1);
            anoMes = YearMonth.of(proximoMes.getYear(), proximoMes.getMonth());
        }

        // Garante que o diaFechamento nunca ultrapasse o último dia do mês
        int ultimoDiaDoMes = anoMes.lengthOfMonth();
        int diaVencimentoAjustado = Math.min(diaFechamento, ultimoDiaDoMes);

        return LocalDate.of(anoMes.getYear(), anoMes.getMonth(), diaVencimentoAjustado);
    }

    public List<DespesaCartaoDTO> buscarDespesasPorCartaoEMes(UUID cartaoId, int ano, int mes) {
        YearMonth anoMes = YearMonth.of(ano, mes);
        LocalDate inicio = anoMes.atDay(1);
        LocalDate fim = anoMes.atEndOfMonth();

        return despesaRepository.findByCartaoAndDataBetween(cartaoId, inicio, fim)
                .stream()
                .map(despesa -> new DespesaCartaoDTO(
                        despesa.getValor(),
                        despesa.getData(),
                        despesa.getCategoria().getNome(),
                        despesa.getDescricao()

                ))
                .collect(Collectors.toList());
    }

}
