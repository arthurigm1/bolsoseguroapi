package bolsoseguroapi.Service;

import bolsoseguroapi.Dto.Conta.ContaCadastroDTO;
import bolsoseguroapi.Dto.Conta.ContaGetDTO;
import bolsoseguroapi.Dto.Conta.ContaSaldoDTO;
import bolsoseguroapi.Dto.Conta.ContaUpdateDTO;
import bolsoseguroapi.Model.Conta;
import bolsoseguroapi.Model.Usuario;
import bolsoseguroapi.Repository.ContaRepository;
import bolsoseguroapi.Security.SecurityService;
import bolsoseguroapi.Validador.ContaValidador;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
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
        conta.setBancoTipo(contaDTO.bancoTipo());
        return contaRepository.save(conta);
    }





        public List<ContaGetDTO> listarContasPorUsuario() {
            Usuario usuario = securityService.obterUsuarioLogado();
            List<Conta> contas = contaRepository.findByUsuario(usuario); // Substitua com sua lógica de consulta

            return contas.stream()
                    .map(conta -> new ContaGetDTO(conta.getId(), conta.getBanco()))
                    .collect(Collectors.toList());
        }


    public List<ContaSaldoDTO> listarSaldoContas() {
        Usuario usuario = securityService.obterUsuarioLogado();
        List<Conta> contas = contaRepository.findByUsuario(usuario); // Substitua com sua lógica de consulta

        return contas.stream()
                .map(conta -> new ContaSaldoDTO(conta.getId(), conta.getBanco(),conta.getSaldo()))
                .collect(Collectors.toList());
    }





    public ContaUpdateDTO atualizarConta(UUID id, ContaUpdateDTO dto) throws AccessDeniedException {
        Usuario usuario = securityService.obterUsuarioLogado();
       Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada com o ID: " + id));
        if (!conta.getUsuario().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Você não tem permissão para atualizar esta conta");
        }

        if (dto.banco() != null) {
            conta.setBanco(dto.banco());
        }

        if (dto.saldo() != null) {
            conta.setSaldo(dto.saldo());
        }

        Conta contaAtualizada = contaRepository.save(conta);
        return new ContaUpdateDTO(contaAtualizada.getBanco(), contaAtualizada.getSaldo());
    }




    public void deletarConta(UUID id) throws AccessDeniedException {
        Usuario usuario = securityService.obterUsuarioLogado();

        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada com o ID: " + id));
        // Verifica se a conta pertence ao usuário logado
        if (!conta.getUsuario().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Você não tem permissão para deletar esta conta");
        }

        contaRepository.deleteById(id);
    }
}