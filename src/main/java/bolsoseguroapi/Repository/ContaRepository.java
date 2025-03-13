package bolsoseguroapi.Repository;

import bolsoseguroapi.Model.Conta;
import bolsoseguroapi.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContaRepository extends JpaRepository<Conta, UUID> {

    List<Conta> findByUsuario(Usuario usuario);
}
