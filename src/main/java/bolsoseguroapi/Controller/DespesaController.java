package bolsoseguroapi.Controller;

import bolsoseguroapi.Dto.Transacao.DespesaDTO;
import bolsoseguroapi.Dto.Transacao.ReceitaDTO;
import bolsoseguroapi.Service.DespesaService;
import bolsoseguroapi.Service.ReceitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/despesa")
public class DespesaController {

    @Autowired
    private DespesaService despesaService;



    @PostMapping
    public ResponseEntity<DespesaDTO> adicionarReceita(@RequestBody DespesaDTO despesaDTO) {
        despesaService.adicionarDespesa(despesaDTO);
        return ResponseEntity.ok().build();
    }
}
