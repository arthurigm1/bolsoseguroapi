package bolsoseguroapi.Repository;

import bolsoseguroapi.Model.Conta;
import bolsoseguroapi.Model.Despesa;
import bolsoseguroapi.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DespesaRepository extends JpaRepository<Despesa, UUID> {

    @Query("SELECT d FROM Despesa d WHERE d.conta IN :contas ORDER BY d.dataCadastro DESC")
    List<Despesa> findDespesasByContasOrderByDataCadastroDesc(@Param("contas") List<Conta> contas);

    @Query("""
        SELECT COALESCE(SUM(d.valor), 0) 
        FROM Despesa d 
        WHERE d.data BETWEEN :startDate AND :endDate 
        AND d.conta IN :contas
    """)
    BigDecimal calcularTotalDespesasMensalPorContas(List<Conta> contas, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COALESCE(SUM(d.valor), 0) FROM Despesa d WHERE d.data BETWEEN :dataInicial AND :dataFinal")
    BigDecimal calcularTotalDespesasMensal(LocalDate dataInicial, LocalDate dataFinal);

    List<Despesa> findByContaInOrderByDataCadastroDesc(List<Conta> contas);

    @Query("SELECT d FROM Despesa d WHERE d.conta IN :contas AND d.data BETWEEN :inicio AND :fim ORDER BY d.data DESC")
    List<Despesa> findDespesasByContasAndDataBetween(@Param("contas") List<Conta> contas, @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT d FROM Despesa d WHERE d.cartao.id = :cartaoId AND d.data BETWEEN :inicio AND :fim")
    List<Despesa> findByCartaoAndDataBetween(
            @Param("cartaoId") UUID cartaoId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

}

