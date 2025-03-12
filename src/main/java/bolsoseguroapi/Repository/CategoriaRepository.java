package bolsoseguroapi.Repository;

import bolsoseguroapi.Model.Categoria;
import bolsoseguroapi.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID  > {
    List<Categoria> findAllByNomeIn(List<String> nomes);

    long countByUsuario(Usuario usuario);

    boolean existsByNomeAndUsuario(String nome, Usuario usuario);

    List<Categoria> findByUsuario(Usuario usuario);

    @Query("SELECT c FROM Categoria c WHERE c.usuario IS NULL")
    List<Categoria> findCategoriasFixas();


    List<Categoria> findCategoriasPersonalizadasByUsuarioId(UUID usuarioId);
}