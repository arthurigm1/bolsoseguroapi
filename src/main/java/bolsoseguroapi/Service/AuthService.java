package bolsoseguroapi.Service;


import bolsoseguroapi.Config.TokenService;
import bolsoseguroapi.Dto.Usuario.*;
import bolsoseguroapi.Exceptions.RegistroException;
import bolsoseguroapi.Model.Conta;
import bolsoseguroapi.Model.Enum.UsuarioRole;
import bolsoseguroapi.Model.PasswordResetToken;
import bolsoseguroapi.Repository.PasswordTokenRepository;
import bolsoseguroapi.Repository.UsuarioRepository;

import bolsoseguroapi.Model.Usuario;
import bolsoseguroapi.Security.SecurityService;
import bolsoseguroapi.Utill.RandomString;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final MailService  mailService;
    private final UsuarioService usuarioService;
    private final PasswordResetService passwordResetService;
    private final PasswordTokenRepository passwordResetTokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final SecurityService securityService;

    public Object login(@Valid LoginRequestDto body) {
        Usuario user = this.repository.findByEmail(body.email());
        if (user == null) {
            return new ErrorResponseDto("Usuário não encontrado");
        }
        if (passwordEncoder.matches(body.senha(), user.getSenha())) {
            String token = this.tokenService.generateToken(user);
            return new SuccessResponseDto(user.getEmail(), token);
        }
        return new ErrorResponseDto("Senha incorreta");
    }

    public ResponseDto registerUser(RegisterRequestDto body) throws UnsupportedEncodingException, MessagingException {
        Optional<Usuario> existingUser = Optional.ofNullable(repository.findByEmail(body.email()));
        if (existingUser.isPresent()) {
            throw new RegistroException("Este e-mail já está cadastrado.");
        }
        Usuario newUser = new Usuario();
        newUser.setNome(body.nome());
        newUser.setEmail(body.email());
        newUser.setRole(UsuarioRole.USER);
        newUser.setSenha(passwordEncoder.encode(body.senha()));
        String randomCode = RandomString.generateRandomString(64);
        newUser.setVerificationCode(randomCode);
        newUser.setEnabled(false);

        Conta contaInicial = new Conta();
        contaInicial.setBanco("Conta Inicial");
        contaInicial.setSaldo(BigDecimal.ZERO);
        contaInicial.setUsuario(newUser);
        newUser.setContaInicial(contaInicial);

        Usuario savedUser = repository.save(newUser);

        try {
            mailService.sendVerificationEmail(savedUser);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Erro ao enviar o e-mail de verificação", e);
        }
        return new ResponseDto(savedUser.getNome(), null, savedUser.getId());
    }




    public String verifyUser(String code) {
        return usuarioService.verify(code) ? "verify_success" : "verify_fail";
    }




    public Map<String, String> forgotPassword(Map<String, String> request) throws MessagingException, UnsupportedEncodingException {
        String email = request.get("email");
        Usuario usuario = repository.findByEmail(email);
        if (usuario == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Usuário não encontrado.");
            return response;
        }
        passwordResetTokenRepository.deleteByUsuarioId(usuario.getId());
        passwordResetService.enviarEmailRecuperacao(email);
        Map<String, String> response = new HashMap<>();
        response.put("message", "E-mail de recuperação enviado!");
        return response;
    }


    public Map<String, String> resetPassword(Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("password");

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido ou expirado"));

        Usuario usuario = resetToken.getUsuario();
        usuario.setSenha(passwordEncoder.encode(newPassword));
        repository.save(usuario);
        passwordResetTokenRepository.delete(resetToken);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Senha redefinida com sucesso!");
        return response;
    }

    public boolean alterarSenha( AlterarSenhadto alterarSenhaDTO) {
        Usuario usuario = securityService.obterUsuarioLogado();
        if (!passwordEncoder.matches(alterarSenhaDTO.senhaAtual(), usuario.getSenha())) {
            return false;
        }
        usuario.setSenha(passwordEncoder.encode(alterarSenhaDTO.novaSenha()));
        usuarioRepository.save(usuario);
        return true;
    }

    public boolean verify(String verificationCode){

        Usuario user = usuarioRepository.findByVerificationCode(verificationCode);

        if(user == null || user.isEnabled()){
            return false;
        }
        user.setVerificationCode(null);
        user.setEnabled(true);
        usuarioRepository.save(user);
        return true;
    }
}
