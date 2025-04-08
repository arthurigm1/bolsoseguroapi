package bolsoseguroapi.Service;

import bolsoseguroapi.Dto.Transacao.BalancoMensalDetalhadoDTO;
import bolsoseguroapi.Dto.Transacao.TransacaoDTO;
import bolsoseguroapi.Dto.Transacao.TransacaoDetalhadaDTO;
import bolsoseguroapi.Model.Conta;
import bolsoseguroapi.Model.Despesa;
import bolsoseguroapi.Model.Receita;
import bolsoseguroapi.Repository.ContaRepository;
import bolsoseguroapi.Repository.DespesaRepository;
import bolsoseguroapi.Repository.ReceitaRepository;
import bolsoseguroapi.Model.Usuario;
import bolsoseguroapi.Security.SecurityService;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;


import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TransacaoService {


    private final ReceitaRepository receitaRepository;


    private final DespesaRepository despesaRepository;


    private final SecurityService securityService;

    private final ContaRepository contaRepository;

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

    public List<TransacaoDetalhadaDTO> obterTransacoesPorMes(int mes, int ano) {
        // Obter o usuário logado
        Usuario usuario = securityService.obterUsuarioLogado();
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        // Obter todas as contas do usuário
        List<Conta> contasDoUsuario = contaRepository.findByUsuario(usuario);

        // Definir período com base no mês e ano informados
        LocalDate inicioMes = LocalDate.of(ano, mes, 1);
        LocalDate fimMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

        // Buscar receitas e despesas dentro do período
        List<Receita> receitas = receitaRepository.findReceitasByContasAndDataBetween(contasDoUsuario, inicioMes, fimMes);
        List<Despesa> despesas = despesaRepository.findDespesasByContasAndDataBetween(contasDoUsuario, inicioMes, fimMes);

        // Combinar receitas e despesas
        List<Object> transacoes = new ArrayList<>();
        transacoes.addAll(receitas);
        transacoes.addAll(despesas);

        // Ordenar pela data (mais recente primeiro)
        transacoes.sort((t1, t2) -> {
            LocalDate data1 = (t1 instanceof Receita) ? ((Receita) t1).getData() : ((Despesa) t1).getData();
            LocalDate data2 = (t2 instanceof Receita) ? ((Receita) t2).getData() : ((Despesa) t2).getData();
            return data2.compareTo(data1);
        });

        // Mapear para DTO detalhado
        return transacoes.stream()
                .map(transacao -> {
                    if (transacao instanceof Receita) {
                        Receita receita = (Receita) transacao;
                        return new TransacaoDetalhadaDTO(
                                receita.getConta().getBanco(),
                                receita.getValor(),
                                "RECEITA",
                                receita.getCategoria().getNome(), // Ajuste conforme sua entidade
                                receita.getData(),
                                receita.getDescricao()
                        );
                    } else {
                        Despesa despesa = (Despesa) transacao;
                        return new TransacaoDetalhadaDTO(
                                despesa.getConta().getBanco(),
                                despesa.getValor(),
                                "DESPESA",
                                despesa.getCategoria().getNome(), // Ajuste conforme sua entidade
                                despesa.getData(),
                                despesa.getDescricao()
                        );
                    }
                })
                .collect(Collectors.toList());
    }

    public byte[] gerarRelatorioTransacaoMensal(int mes, int ano) throws DocumentException, IOException {
        Usuario usuario = securityService.obterUsuarioLogado();
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        // Obter as transações do mês informado
        List<TransacaoDetalhadaDTO> transacoes = obterTransacoesPorMes(mes, ano);

        // Configurar o documento PDF
        Document document = new Document(PageSize.A4.rotate()); // Formato paisagem
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, outputStream);
        document.open();

        String logoPath = "templates/logo.png"; // Caminho dentro do classpath
        Image logo = Image.getInstance(getClass().getClassLoader().getResource(logoPath));
        logo.scaleToFit(120, 60);
        logo.setAlignment(Element.ALIGN_LEFT);
        document.add(logo);

        // Adicionar título do relatório
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
        Paragraph title = new Paragraph("Relatório Financeiro Mensal", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20f);
        document.add(title);

        // Adicionar período do relatório
        Font periodFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.GRAY);
        String periodo = LocalDate.of(ano, mes, 1).format(DateTimeFormatter.ofPattern("MMMM/yyyy"));
        Paragraph period = new Paragraph("Período: " + periodo, periodFont);
        period.setAlignment(Element.ALIGN_CENTER);
        period.setSpacingAfter(20f);
        document.add(period);

        // Criar tabela para as transações
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Configurar cabeçalho da tabela
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        PdfPCell headerCell;

        String[] headers = {"Data", "Tipo", "Categoria", "Banco", "Descrição", "Valor (R$)"};
        for (String header : headers) {
            headerCell = new PdfPCell(new Phrase(header, headerFont));
            headerCell.setBackgroundColor(new BaseColor(70, 130, 180)); // Azul steel
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPadding(5f);
            table.addCell(headerCell);
        }

        // Adicionar transações à tabela
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        for (TransacaoDetalhadaDTO transacao : transacoes) {
            table.addCell(new PdfPCell(new Phrase(transacao.getData().format(dateFormatter), cellFont))); // Data
            PdfPCell tipoCell = new PdfPCell(new Phrase(transacao.getTipo(), cellFont));
            tipoCell.setPadding(5f);
            tipoCell.setBackgroundColor("RECEITA".equals(transacao.getTipo()) ? new BaseColor(220, 255, 220) : new BaseColor(255, 220, 220));
            table.addCell(tipoCell); // Tipo

            table.addCell(new PdfPCell(new Phrase(transacao.getCategoria(), cellFont))); // Categoria
            table.addCell(new PdfPCell(new Phrase(transacao.getBanco(), cellFont))); // Banco
            table.addCell(new PdfPCell(new Phrase(transacao.getDescricao(), cellFont))); // Descrição

            PdfPCell valorCell = new PdfPCell(new Phrase(currencyFormat.format(transacao.getValor()), cellFont));
            valorCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(valorCell); // Valor
        }

        document.add(table);

        // Adicionar totais
        Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        double totalReceitas = transacoes.stream().filter(t -> "RECEITA".equals(t.getTipo())).mapToDouble(t -> t.getValor().doubleValue()).sum();
        double totalDespesas = transacoes.stream().filter(t -> "DESPESA".equals(t.getTipo())).mapToDouble(t -> t.getValor().doubleValue()).sum();
        double saldo = totalReceitas - totalDespesas;

        Paragraph receitasTotal = new Paragraph("Total de Receitas: " + currencyFormat.format(totalReceitas), totalFont);
        receitasTotal.setSpacingBefore(15f);
        document.add(receitasTotal);

        Paragraph despesasTotal = new Paragraph("Total de Despesas: " + currencyFormat.format(totalDespesas), totalFont);
        document.add(despesasTotal);

        Paragraph saldoTotal = new Paragraph("Saldo do Mês: " + currencyFormat.format(saldo), totalFont);
        saldoTotal.setSpacingAfter(15f);
        saldoTotal.setFont(saldo >= 0 ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.GREEN) : FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.RED));
        document.add(saldoTotal);

        // Adicionar rodapé
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.GRAY);
        Paragraph footer = new Paragraph("Relatório gerado em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();

        return outputStream.toByteArray();
    }


}
