package bolsoseguroapi.Repository;

import bolsoseguroapi.Model.Conta;
import bolsoseguroapi.Model.Receita;
import bolsoseguroapi.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReceitaRepository extends JpaRepository<Receita, UUID> {
    List<Receita> findByContaInOrderByDataCadastroDesc(List<Conta> contas);

    @Query("SELECT r FROM Receita r WHERE r.conta IN :contas ORDER BY r.dataCadastro DESC")
    List<Receita> findReceitasByContasOrderByDataCadastroDesc(@Param("contas") List<Conta> contas);

    @Query("""
        SELECT COALESCE(SUM(d.valor), 0) 
        FROM Receita d 
        WHERE d.data BETWEEN :startDate AND :endDate 
        AND d.conta IN :contas
    """)
    BigDecimal calcularTotalReceitasMensalPorContas(List<Conta> contas, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COALESCE(SUM(r.valor), 0) FROM Receita r WHERE r.data BETWEEN :dataInicial AND :dataFinal")
    BigDecimal calcularTotalReceitasMensal(LocalDate dataInicial, LocalDate dataFinal);

    List<Receita> findByContaUsuarioAndCategoriaId( Usuario usuario, Long categoriaId);
}