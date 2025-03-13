package bolsoseguroapi.Controller;

import bolsoseguroapi.Dto.Transacao.TransacaoDTO;
import bolsoseguroapi.Service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;


    @GetMapping("")
    public List<TransacaoDTO> obterUltimasTransacoes() {
        return transacaoService.obterUltimasTransacoesDeTodasContas();
    }
}