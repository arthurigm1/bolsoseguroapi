package bolsoseguroapi.Dto.Transacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransacaoDetalhadaDTO {
    private String banco;

    private BigDecimal valor;
    private String tipo; // "RECEITA" ou "DESPESA"
    private String categoria;
    private LocalDate data;
    private String descricao;

    // Construtor
    public TransacaoDetalhadaDTO(String banco, BigDecimal valor, String tipo,
                                 String categoria, LocalDate data, String descricao) {
        this.banco = banco;

        this.valor = valor;
        this.tipo = tipo;
        this.categoria = categoria;
        this.data = data;
        this.descricao = descricao;
    }

    // Getters e Setters
    public String getBanco() { return banco; }

    public BigDecimal getValor() { return valor; }
    public String getTipo() { return tipo; }
    public String getCategoria() { return categoria; }
    public LocalDate getData() { return data; }
    public String getDescricao() { return descricao; }
}