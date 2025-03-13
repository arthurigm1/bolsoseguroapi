package bolsoseguroapi.Repository;

import bolsoseguroapi.Model.MetaFinanceira;
import bolsoseguroapi.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MetaFinanceiraRepository extends JpaRepository<MetaFinanceira, UUID> {
    List<MetaFinanceira> findByUsuario(Usuario usuario);
}
