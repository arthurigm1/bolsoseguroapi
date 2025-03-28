package bolsoseguroapi.Service;

import bolsoseguroapi.Dto.Categoria.CategoriaDTO;
import bolsoseguroapi.Exceptions.CategoriaLimitException;
import bolsoseguroapi.Model.Categoria;
import bolsoseguroapi.Model.Conta;
import bolsoseguroapi.Model.Enum.TipoCategoria;
import bolsoseguroapi.Model.Usuario;
import bolsoseguroapi.Repository.CategoriaRepository;
import bolsoseguroapi.Repository.UsuarioRepository;
import bolsoseguroapi.Security.SecurityService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private SecurityService securityService;

    private static final List<Categoria> CATEGORIAS_FIXAS = List.of(
            new Categoria("Alimentação", true, TipoCategoria.DESPESA),
            new Categoria("Transporte", true, TipoCategoria.DESPESA),
            new Categoria("Saúde", true, TipoCategoria.DESPESA),
            new Categoria("Educação", true, TipoCategoria.DESPESA),
            new Categoria("Lazer", true, TipoCategoria.DESPESA),
            new Categoria("Salário", true, TipoCategoria.RECEITA),
            new Categoria("Investimentos", true, TipoCategoria.RECEITA),
            new Categoria("Empréstimos", true, TipoCategoria.RECEITA),
            new Categoria("Pagamento Cartão",true,TipoCategoria.DESPESA)
    );

    @PostConstruct
    public void inicializarCategoriasFixas() {
        // Obtém os nomes das categorias existentes para evitar buscas repetidas
        List<String> nomesExistentes = categoriaRepository.findAll().stream()
                .map(Categoria::getNome)
                .toList();

        // Filtra apenas as categorias que ainda não existem e adiciona ao banco
        CATEGORIAS_FIXAS.stream()
                .filter(categoria -> !nomesExistentes.contains(categoria.getNome()))
                .forEach(categoriaRepository::save);
    }

    public Categoria criarCategoriaPersonalizada(String nomeCategoria, TipoCategoria tipo) {
        Usuario usuario = securityService.obterUsuarioLogado();

        // Verifica se o usuário já atingiu o limite de 5 categorias personalizadas
        if (categoriaRepository.countByUsuario(usuario) >= 5) {
            throw new CategoriaLimitException("Você já atingiu o limite de 5 categorias personalizadas.");
        }

        // Verifica se já existe uma categoria com o mesmo nome e tipo para esse usuário
        if (categoriaRepository.existsByNomeAndUsuarioAndTipo(nomeCategoria, usuario, tipo)) {
            throw new CategoriaLimitException("Já existe uma categoria do tipo " + tipo + " com esse nome para este usuário.");
        }

        // Cria a nova categoria personalizada
        Categoria categoria = new Categoria(nomeCategoria, false, tipo);
        categoria.setUsuario(usuario);

        return categoriaRepository.save(categoria);
    }

    public List<CategoriaDTO> getCategoriasFixas() {
        return categoriaRepository.findCategoriasFixas().stream()
                .map(categoria -> new CategoriaDTO(categoria.getId(), categoria.getNome(), true, categoria.getTipo()))
                .collect(Collectors.toList());
    }

    public List<CategoriaDTO> getCategoriasPersonalizadasDoUsuario() {
        Usuario usuario = securityService.obterUsuarioLogado();
        return categoriaRepository.findCategoriasPersonalizadasByUsuarioId(usuario.getId()).stream()
                .map(categoria -> new CategoriaDTO(categoria.getId(), categoria.getNome(), false, categoria.getTipo()))
                .collect(Collectors.toList());
    }

    public List<CategoriaDTO> getCategoriasPorTipo(TipoCategoria tipo) {
        Usuario usuario = securityService.obterUsuarioLogado();
        return categoriaRepository.findByTipo(tipo).stream()
                .filter(categoria -> (categoria.isFixa() || categoria.getUsuario().equals(usuario))) // Filtra por categorias fixas ou do usuário
                .map(categoria -> new CategoriaDTO(categoria.getId(), categoria.getNome(), categoria.isFixa(), categoria.getTipo()))
                .collect(Collectors.toList());
    }

    public void deletarCategoria(Long id) throws AccessDeniedException {
        Usuario usuario = securityService.obterUsuarioLogado();

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada com o ID: " + id));


        if (!categoria.getUsuario().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Você não tem permissão para deletar esta Categoria");
        }

        categoriaRepository.deleteById(id);
    }
}
