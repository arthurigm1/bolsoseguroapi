package bolsoseguroapi.Controller;

import bolsoseguroapi.Dto.Transacao.ReceitaDTO;
import bolsoseguroapi.Model.Receita;
import bolsoseguroapi.Service.ReceitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/receita")
public class ReceitaController {
    @Autowired
    private  ReceitaService receitaService;



    @PostMapping
    public ResponseEntity<ReceitaDTO> adicionarReceita(@RequestBody ReceitaDTO receitaDTO) {
         receitaService.adicionarReceita(receitaDTO);
        return ResponseEntity.ok().build();
    }
}