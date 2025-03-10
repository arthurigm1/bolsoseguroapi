package bolsoseguroapi.Service;

import bolsoseguroapi.Model.Usuario;
import bolsoseguroapi.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository userRepository;

    public Usuario obterPorEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
