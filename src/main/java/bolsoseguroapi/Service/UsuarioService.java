package bolsoseguroapi.Service;

import bolsoseguroapi.Config.AuthenticatedUserProvider;
import bolsoseguroapi.Dto.Usuario.SaldoResponseDTO;
import bolsoseguroapi.Dto.Usuario.UsuarioInfoResponse;
import bolsoseguroapi.Model.Usuario;
import bolsoseguroapi.Repository.UsuarioRepository;
import bolsoseguroapi.Security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    @Autowired
    private  UsuarioRepository userRepository;

    private final AuthenticatedUserProvider authenticatedUserProvider;

    public Usuario obterPorEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public SaldoResponseDTO obterSaldo() {
        String email = authenticatedUserProvider.getAuthenticatedUsername();
        Usuario usuario = userRepository.findByEmail(email);
        return new SaldoResponseDTO(usuario.getNome(), usuario.getSaldoGeral());
    }

    public UsuarioInfoResponse getUsuario()
    {   String email = authenticatedUserProvider.getAuthenticatedUsername();
        Usuario usuario = userRepository.findByEmail(email);
        return new UsuarioInfoResponse(usuario.getNome(),usuario.getEmail(),usuario.isEnabled(),usuario.getSaldoGeral());

    }

    public boolean verify(String verificationCode){

        Usuario user = userRepository.findByVerificationCode(verificationCode);

        if(user == null || user.isEnabled()){
            return false;
        }
        user.setVerificationCode(null);
        user.setEnabled(true);
        userRepository.save(user);
        return true;
    }

}
