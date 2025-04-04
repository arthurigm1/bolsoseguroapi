package bolsoseguroapi.Repository;

import bolsoseguroapi.Model.Cartao;
import bolsoseguroapi.Model.FaturaCartao;
import bolsoseguroapi.Model.MetaFinanceira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FaturaRepository extends JpaRepository<FaturaCartao, UUID> {
    Optional<FaturaCartao> findByCartaoAndDataVencimento(Cartao cartao, LocalDate dataVencimento);
    List<FaturaCartao> findByCartaoAndDataVencimentoBetween(Cartao cartao, LocalDate inicio, LocalDate fim);

}
