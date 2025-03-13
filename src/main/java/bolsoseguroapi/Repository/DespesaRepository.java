package bolsoseguroapi.Repository;

import bolsoseguroapi.Model.Conta;
import bolsoseguroapi.Model.Despesa;
import bolsoseguroapi.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.util.List;
import java.util.UUID;

public interface DespesaRepository extends JpaRepository<Despesa, UUID> {

    @Query("SELECT d FROM Despesa d WHERE d.conta IN :contas ORDER BY d.dataCadastro DESC")
    List<Despesa> findDespesasByContasOrderByDataCadastroDesc(@Param("contas") List<Conta> contas);

    List<Despesa> findByContaInOrderByDataCadastroDesc(List<Conta> contas);
}

