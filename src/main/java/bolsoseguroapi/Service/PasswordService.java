package bolsoseguroapi.Service;

import bolsoseguroapi.Model.PasswordResetToken;
import bolsoseguroapi.Model.Usuario;
import bolsoseguroapi.Repository.PasswordTokenRepository;
import bolsoseguroapi.Repository.UsuarioRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class PasswordService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordTokenRepository tokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordResetService passwordResetService;


    public String resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido ou expirado"));

        Usuario usuario = resetToken.getUsuario();
        usuario.setSenha(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
        tokenRepository.delete(resetToken);
        return "Senha redefinida com sucesso!";
    }

    public String forgotPassword(String email) throws MessagingException, UnsupportedEncodingException {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado.");
        }
        tokenRepository.deleteByUsuarioId(usuario.getId());
        passwordResetService.enviarEmailRecuperacao(email);
        return "E-mail de recuperação enviado!";
    }
}
