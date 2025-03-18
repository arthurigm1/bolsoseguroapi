package bolsoseguroapi.Service;

import bolsoseguroapi.Dto.MetaFinanceira.MetaFinanceiraRequestDTO;
import bolsoseguroapi.Dto.MetaFinanceira.MetaFinanceiraResponseDTO;
import bolsoseguroapi.Model.MetaFinanceira;
import bolsoseguroapi.Model.Usuario;
import bolsoseguroapi.Repository.MetaFinanceiraRepository;
import bolsoseguroapi.Security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MetaFinanceiraService {
    @Autowired
    private  MetaFinanceiraRepository metaRepository;
    @Autowired
    private  SecurityService securityService;

    public MetaFinanceiraService(MetaFinanceiraRepository metaRepository, SecurityService securityService) {
        this.metaRepository = metaRepository;
        this.securityService = securityService;
    }

    // Criar uma nova meta financeira
    public MetaFinanceiraResponseDTO criarMeta(MetaFinanceiraRequestDTO dto) {
        Usuario usuario = securityService.obterUsuarioLogado();

        MetaFinanceira meta = new MetaFinanceira();
        meta.setUsuario(usuario);
        meta.setNome(dto.nome());
        meta.setValorMeta(dto.valorMeta());
        meta.setValorAtual(BigDecimal.ZERO); // Inicializa com R$ 0,00

        metaRepository.save(meta);

        return converterParaDTO(meta);
    }

    // Listar todas as metas do usuário logado
    public List<MetaFinanceiraResponseDTO> listarMetasUsuario() {
        Usuario usuario = securityService.obterUsuarioLogado();
        return metaRepository.findByUsuario(usuario)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    // Atualizar o valor poupado na meta
    public MetaFinanceiraResponseDTO adicionarValor(UUID metaId, BigDecimal valorAdicionado) {
        MetaFinanceira meta = metaRepository.findById(metaId)
                .orElseThrow(() -> new RuntimeException("Meta não encontrada!"));

        meta.setValorAtual(meta.getValorAtual().add(valorAdicionado));
        metaRepository.save(meta);

        return converterParaDTO(meta);
    }

    // Verificar se a meta foi atingida
    public boolean verificarMetaAtingida(UUID metaId) {
        MetaFinanceira meta = metaRepository.findById(metaId)
                .orElseThrow(() -> new RuntimeException("Meta não encontrada!"));
        return meta.atingiuMeta();
    }


    private MetaFinanceiraResponseDTO converterParaDTO(MetaFinanceira meta) {
        return new MetaFinanceiraResponseDTO(
                meta.getId(),
                meta.getNome(),
                meta.getValorAtual(),
                meta.getValorMeta(),
                meta.atingiuMeta()
        );
    }
}
