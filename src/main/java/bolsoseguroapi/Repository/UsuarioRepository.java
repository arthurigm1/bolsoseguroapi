package bolsoseguroapi.Repository;

import bolsoseguroapi.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Usuario findByEmail(String email);
    Optional<Usuario> findById(UUID id);



    Usuario findByVerificationCode(String verificationCode);

}
