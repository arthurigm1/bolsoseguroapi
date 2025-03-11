package bolsoseguroapi.Service;


import bolsoseguroapi.Dto.Categoria.CategoriaDTO;
import bolsoseguroapi.Exceptions.CategoriaLimitException;
import bolsoseguroapi.Model.Categoria;
import bolsoseguroapi.Model.Usuario;
import bolsoseguroapi.Repository.CategoriaRepository;
import bolsoseguroapi.Repository.UsuarioRepository;
import bolsoseguroapi.Security.SecurityService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository; // Para buscar usuários

    @Autowired
    private SecurityService securityService;

    private static final List<String> CATEGORIAS_FIXAS = List.of(
            "Alimentação", "Transporte", "Saúde", "Educação", "Lazer"
    );

    @PostConstruct
    public void inicializarCategoriasFixas() {

        List<String> categoriasExistentes = categoriaRepository.findAllByNomeIn(CATEGORIAS_FIXAS)
                .stream().map(Categoria::getNome).collect(Collectors.toList());

        // Filtra e adiciona somente as que ainda não existem
        CATEGORIAS_FIXAS.stream()
                .filter(nome -> !categoriasExistentes.contains(nome))
                .forEach(nome -> categoriaRepository.save(new Categoria(nome, true)));
    }

    public Categoria criarCategoriaPersonalizada(String nomeCategoria) {
        Usuario usuario = securityService.obterUsuarioLogado();
        usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Verificar se o usuário já atingiu o limite de 5 categorias personalizadas
        if (categoriaRepository.countByUsuario(usuario) >= 5) {
            throw new CategoriaLimitException("Você já atingiu o limite de 5 categorias personalizadas.");
        }

        // Verificar se já existe uma categoria com o mesmo nome para esse usuário
        if (categoriaRepository.existsByNomeAndUsuario(nomeCategoria, usuario)) {
            throw new CategoriaLimitException("Categoria já existe para este usuário.");
        }

        Categoria categoria = new Categoria();
        categoria.setNome(nomeCategoria);
        categoria.setFixa(false);
        categoria.setUsuario(usuario);

        return categoriaRepository.save(categoria);
    }

    public List<CategoriaDTO> getCategoriasFixas() {
        // Supondo que as categorias fixas são categorias que não pertencem a nenhum usuário específico
        List<Categoria> categoriasFixas = categoriaRepository.findCategoriasFixas();
        return categoriasFixas.stream()
                .map(categoria -> new CategoriaDTO(categoria.getId(), categoria.getNome(), true))
                .collect(Collectors.toList());
    }

    public List<CategoriaDTO> getCategoriasPersonalizadasDoUsuario() {
        Usuario usuario = securityService.obterUsuarioLogado();
        List<Categoria> categoriasPersonalizadas = categoriaRepository.findCategoriasPersonalizadasByUsuarioId(usuario.getId());
        return categoriasPersonalizadas.stream()
                .map(categoria -> new CategoriaDTO(categoria.getId(), categoria.getNome(), false))
                .collect(Collectors.toList());
    }
}