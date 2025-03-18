package bolsoseguroapi.Service;

import bolsoseguroapi.Dto.Conta.ContaCadastroDTO;
import bolsoseguroapi.Dto.Conta.ContaGetDTO;
import bolsoseguroapi.Model.Conta;
import bolsoseguroapi.Model.Usuario;
import bolsoseguroapi.Repository.ContaRepository;
import bolsoseguroapi.Security.SecurityService;
import bolsoseguroapi.Validador.ContaValidador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;
    @Autowired
    private SecurityService securityService;


    public Conta cadastrarConta(ContaCadastroDTO contaDTO) {
        ContaValidador.validar(contaDTO);
        Usuario usuario = securityService.obterUsuarioLogado();
        Conta conta = new Conta();
        conta.setUsuario(usuario);
        conta.setBanco(contaDTO.banco());
        conta.setSaldo(contaDTO.saldo());
        return contaRepository.save(conta);
    }





        public List<ContaGetDTO> listarContasPorUsuario() {
            Usuario usuario = securityService.obterUsuarioLogado();
            List<Conta> contas = contaRepository.findByUsuario(usuario); // Substitua com sua lógica de consulta

            return contas.stream()
                    .map(conta -> new ContaGetDTO(conta.getId(), conta.getBanco()))
                    .collect(Collectors.toList());
        }


    public Optional<Conta> buscarContaPorId(UUID id) {
        return contaRepository.findById(id);
    }


    public Conta atualizarConta(UUID id, Conta contaAtualizada) {
        return contaRepository.findById(id)
                .map(conta -> {
                    conta.setBanco(contaAtualizada.getBanco());
                    conta.setSaldo(contaAtualizada.getSaldo());
                    conta.setUsuario(contaAtualizada.getUsuario());
                    return contaRepository.save(conta);
                })
                .orElseThrow(() -> new RuntimeException("Conta não encontrada com o ID: " + id));
    }


    public void deletarConta(UUID id) {
        contaRepository.deleteById(id);
    }
}