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
import java.util.UUID;

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

        // Verifica se a despesa entra na fatura atual ou na próxima
        LocalDate hoje = LocalDate.now();
        LocalDate dataFatura = calcularDataFatura(cartao, dataDespesa);
        boolean faturaAtual = dataFatura.getMonthValue() == hoje.getMonthValue() && dataFatura.getYear() == hoje.getYear();

        if (faturaAtual) {
            cartao.setLimiteDisponivel(cartao.getLimiteDisponivel().subtract(despesa.getValor()));
        }

        // Atualiza o total da fatura do cartão
        cartao.setFaturaAtual(cartao.getFaturaAtual().add(despesa.getValor()));

        cartaoRepository.save(cartao);
    }


    private LocalDate calcularDataFatura(Cartao cartao, LocalDate dataDespesa) {
        int diaFechamento = cartao.getDiaFechamentoFatura();

        // Se a data da despesa for antes ou no dia do fechamento
        if (dataDespesa.getDayOfMonth() <= diaFechamento) {
            // A fatura será do mês atual (fechará no dia de fechamento deste mês)
            return LocalDate.of(dataDespesa.getYear(), dataDespesa.getMonth(), diaFechamento);
        } else {
            // Se for após o dia de fechamento, vai para o mês seguinte
            LocalDate proximoMes = dataDespesa.plusMonths(1);
            return LocalDate.of(proximoMes.getYear(), proximoMes.getMonth(), diaFechamento);
        }
    }
}
