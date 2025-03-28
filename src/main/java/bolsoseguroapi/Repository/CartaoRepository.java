package bolsoseguroapi.Repository;

import bolsoseguroapi.Model.Cartao;
import bolsoseguroapi.Model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CartaoRepository  extends JpaRepository<Cartao, UUID>  {

    List<Cartao> findByUsuarioId(UUID usuarioId);
}
