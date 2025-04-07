package bolsoseguroapi.Service;

import bolsoseguroapi.Controller.DespesaController;
import bolsoseguroapi.Dto.Cartao.FaturaCartaoDTO;
import bolsoseguroapi.Dto.Transacao.DespesaCartaoDTO;
import bolsoseguroapi.Model.*;
import bolsoseguroapi.Repository.*;
import bolsoseguroapi.Security.SecurityService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import static bolsoseguroapi.Model.Enum.TipoCategoria.DESPESA;

@Service
@RequiredArgsConstructor
public class FaturaService {
    private final FaturaRepository faturaRepository;
    private final CartaoRepository cartaoRepository;
    private final ContaRepository contaRepository;
    private final DespesaRepository despesaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final SecurityService securityService;

    private final DespesaService despesaService;

    public List<FaturaCartaoDTO> buscarFaturasPorMes(UUID cartaoId, int mes, int ano) {
        Usuario usuario = securityService.obterUsuarioLogado();
        Cartao cartao = cartaoRepository.findById(cartaoId)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));


        if (!cartao.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso não autorizado a este cartão");
        }

        YearMonth periodo = YearMonth.of(ano, mes);
        LocalDate primeiroDia = periodo.atDay(1);
        LocalDate ultimoDia = periodo.atEndOfMonth();

        List<FaturaCartao> faturas = faturaRepository.findByCartaoAndDataVencimentoBetween(cartao, primeiroDia, ultimoDia);

        return faturas.stream()
                .map(FaturaCartaoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void pagarFatura(UUID cartaoId, int mes, int ano, UUID contaId) {
        Usuario usuario = securityService.obterUsuarioLogado();

        // Busca o cartão
        Cartao cartao = cartaoRepository.findById(cartaoId)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));

        // Verifica se o cartão pertence ao usuário logado
        if (!cartao.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso não autorizado a este cartão");
        }

        // Busca a conta
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        YearMonth periodo = YearMonth.of(ano, mes);
        LocalDate primeiroDia = periodo.atDay(1);
        LocalDate ultimoDia = periodo.atEndOfMonth();

        // Busca a fatura do período especificado
        List<FaturaCartao> faturas = faturaRepository.findByCartaoAndDataVencimentoBetween(cartao, primeiroDia, ultimoDia);

        if (faturas.isEmpty()) {
            throw new RuntimeException("Nenhuma fatura encontrada para este período.");
        }

        // Calcula o total da fatura
        BigDecimal totalFatura = faturas.stream()
                .map(FaturaCartao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        for (FaturaCartao fatura : faturas) {
            if (fatura.isPaga()) {
                throw new RuntimeException("Fatura já foi paga.");
            }
            BigDecimal totalPagoAtual = fatura.getTotalpago() != null ? fatura.getTotalpago() : BigDecimal.ZERO;
            fatura.setTotalpago(totalPagoAtual.add(totalFatura));
            fatura.setValor(BigDecimal.ZERO);

            fatura.setPaga(true);
            fatura.setDataPagamento(LocalDate.now());
            faturaRepository.save(fatura);
        }

        // Atualiza o limite disponível do cartão
        cartao.setLimiteDisponivel(cartao.getLimiteDisponivel().add(totalFatura));
        cartaoRepository.save(cartao);

        // Debita o valor da conta
        conta.setSaldo(conta.getSaldo().subtract(totalFatura));
        contaRepository.save(conta);

        // Busca ou cria a categoria específica para pagamento de fatura
        Categoria categoriaPagamento = categoriaRepository.findByNome("Pagamento de Cartão")
                .orElseGet(() -> {
                    Categoria novaCategoria = new Categoria();
                    novaCategoria.setNome("Pagamento de Cartão");
                    novaCategoria.setTipo(DESPESA);
                    novaCategoria.setFixa(true);
                    return categoriaRepository.save(novaCategoria);
                });

        // Cria a despesa associada ao pagamento
        Despesa despesa = new Despesa();
        despesa.setValor(totalFatura);
        despesa.setData(LocalDate.now());
        despesa.setDescricao("Pagamento fatura cartão " + cartao.getNome() + " - " +
                periodo.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR")));
        despesa.setConta(conta);
        despesa.setCategoria(categoriaPagamento);
        despesaRepository.save(despesa);


        usuario.setSaldoGeral(usuario.getSaldoGeral().subtract(totalFatura));
        usuarioRepository.save(usuario);
    }


    public byte[] exportarFaturasPorMes(UUID cartaoId, int mes, int ano) throws DocumentException, IOException {
        Usuario usuario = securityService.obterUsuarioLogado();


        Cartao cartao = cartaoRepository.findById(cartaoId)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));


        if (!cartao.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso não autorizado a este cartão");
        }
        List<FaturaCartaoDTO> faturas = buscarFaturasPorMes(cartaoId, mes, ano);

        // Verificar se há faturas
        if (faturas.isEmpty()) {
            throw new RuntimeException("Nenhuma fatura encontrada para o período especificado");
        }

        // Configurar o documento PDF
        Document document = new Document(PageSize.A4.rotate()); // Formato paisagem
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Adicionar logo
        String logoPath = "templates/logo.png";
        try {
            Image logo = Image.getInstance(getClass().getClassLoader().getResource(logoPath));
            logo.scaleToFit(120, 60);
            logo.setAlignment(Element.ALIGN_LEFT);
            document.add(logo);
        } catch (Exception e) {
            System.err.println("Logo não encontrado: " + e.getMessage());
        }

        // Adicionar título do relatório
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
        Paragraph title = new Paragraph("Relatório de Faturas de Cartão", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20f);
        document.add(title);

        // Adicionar informações do cartão (pegando da primeira fatura)
        FaturaCartaoDTO primeiraFatura = faturas.get(0);
        Font cardFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        Paragraph cardInfo = new Paragraph();
        cardInfo.add(new Chunk("Cartão: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        cardInfo.add(new Chunk(primeiraFatura.nomeCartao() + " (" + primeiraFatura.bandeira() + ")", cardFont));
        cardInfo.add(Chunk.NEWLINE);
        cardInfo.add(new Chunk("Limite Total: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        cardInfo.add(new Chunk(formatCurrency(primeiraFatura.limiteTotal()), cardFont));
        cardInfo.add(Chunk.NEWLINE);
        cardInfo.add(new Chunk("Limite Disponível: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        cardInfo.add(new Chunk(formatCurrency(primeiraFatura.limiteDisponivel()), cardFont));
        cardInfo.setSpacingAfter(15f);
        document.add(cardInfo);

        // Adicionar período do relatório
        Font periodFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.GRAY);
        String periodo = LocalDate.of(ano, mes, 1).format(DateTimeFormatter.ofPattern("MMMM/yyyy"));
        Paragraph period = new Paragraph("Período da Fatura: " + periodo, periodFont);
        period.setAlignment(Element.ALIGN_CENTER);
        period.setSpacingAfter(20f);
        document.add(period);
        // Buscar despesas do cartão no mês
        List<DespesaCartaoDTO> despesascartao = despesaService.buscarDespesasPorCartaoEMes(cartaoId, ano, mes);

// Se houver despesas, adicioná-las ao relatório
        if (!despesascartao.isEmpty()) {
            // Título da seção
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.DARK_GRAY);
            Paragraph despesasTitle = new Paragraph("Despesas do Cartão no Mês", sectionFont);
            despesasTitle.setSpacingBefore(20f);
            despesasTitle.setSpacingAfter(10f);
            document.add(despesasTitle);

            // Criar tabela de despesas
            PdfPTable despesasTable = new PdfPTable(4); // Data, Descrição, Valor, Categoria
            despesasTable.setWidthPercentage(100);
            despesasTable.setSpacingBefore(10f);

            // Cabeçalhos
            String[] despesaHeaders = {"Data", "Descrição", "Valor (R$)", "Categoria"};
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
            for (String header : despesaHeaders) {
                PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
                headerCell.setBackgroundColor(new BaseColor(100, 149, 237)); // Azul cobalto
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPadding(5f);
                despesasTable.addCell(headerCell);
            }

            // Preencher a tabela
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (DespesaCartaoDTO despesa : despesascartao) {
                // Data
                despesasTable.addCell(new PdfPCell(new Phrase(despesa.data().format(dateFormatter), cellFont)));

                // Descrição
                despesasTable.addCell(new PdfPCell(new Phrase(despesa.descricao(), cellFont)));

                // Valor
                PdfPCell valorCell = new PdfPCell(new Phrase(formatCurrency(despesa.valor()), cellFont));
                valorCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                despesasTable.addCell(valorCell);

                // Categoria
                despesasTable.addCell(new PdfPCell(new Phrase(despesa.categoria(), cellFont)));
            }

            document.add(despesasTable);
        } else {
            Paragraph noDespesas = new Paragraph("Nenhuma despesa registrada para este cartão no período selecionado.",
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY));
            noDespesas.setSpacingBefore(10f);
            document.add(noDespesas);
        }

        // Criar tabela para as faturas
        PdfPTable table = new PdfPTable(4); // Reduzido para 4 colunas conforme os dados disponíveis
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Configurar cabeçalho da tabela
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        String[] headers = {"Data Vencimento", "Valor (R$)", "Status", "Limite Disponível"};
        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
            headerCell.setBackgroundColor(new BaseColor(70, 130, 180)); // Azul steel
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPadding(5f);
            table.addCell(headerCell);
        }

        // Adicionar faturas à tabela
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (FaturaCartaoDTO fatura : faturas) {
            // Data Vencimento
            table.addCell(new PdfPCell(new Phrase(fatura.dataVencimento().format(dateFormatter), cellFont)));

            // Valor
            PdfPCell valorCell = new PdfPCell(new Phrase(formatCurrency(fatura.totalpago()), cellFont));
            valorCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(valorCell);

            // Status
            PdfPCell statusCell = new PdfPCell();
            statusCell.setPadding(5f);

            if (fatura.paga()) {
                statusCell.setPhrase(new Phrase("PAGA", cellFont));
                statusCell.setBackgroundColor(new BaseColor(220, 255, 220)); // Verde claro
            } else {
                if (fatura.dataVencimento().isBefore(LocalDate.now())) {
                    statusCell.setPhrase(new Phrase("VENCIDA", cellFont));
                    statusCell.setBackgroundColor(new BaseColor(255, 220, 220)); // Vermelho claro
                } else {
                    statusCell.setPhrase(new Phrase("ABERTA", cellFont));
                    statusCell.setBackgroundColor(new BaseColor(255, 255, 220)); // Amarelo claro
                }
            }
            table.addCell(statusCell);

            // Limite Disponível
            PdfPCell limiteCell = new PdfPCell(new Phrase(formatCurrency(fatura.limiteDisponivel()), cellFont));
            limiteCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(limiteCell);
        }

        document.add(table);

        // Adicionar totais
        Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        BigDecimal totalFaturas = faturas.stream()
                .map(FaturaCartaoDTO::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPago = faturas.stream()
                .filter(FaturaCartaoDTO::paga)
                .map(FaturaCartaoDTO::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Paragraph faturasTotal = new Paragraph("Total de Faturas: " + formatCurrency(totalFaturas), totalFont);
        faturasTotal.setSpacingBefore(15f);
        document.add(faturasTotal);

        Paragraph pagoTotal = new Paragraph("Total Pago: " + formatCurrency(totalPago), totalFont);
        document.add(pagoTotal);

        Paragraph pendenteTotal = new Paragraph("Total Pendente: " +
                formatCurrency(totalFaturas.subtract(totalPago)), totalFont);
        pendenteTotal.setSpacingAfter(15f);

        // Define cor do texto baseado no saldo pendente
        if (totalFaturas.subtract(totalPago).compareTo(BigDecimal.ZERO) > 0) {
            pendenteTotal.setFont(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.RED));
        } else {
            pendenteTotal.setFont(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.GREEN));
        }
        document.add(pendenteTotal);

        // Adicionar rodapé
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.GRAY);
        Paragraph footer = new Paragraph("Relatório gerado em: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();

        return outputStream.toByteArray();
    }


    private String formatCurrency(BigDecimal value) {
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(value);
    }}
