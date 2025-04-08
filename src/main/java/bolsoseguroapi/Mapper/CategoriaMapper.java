package bolsoseguroapi.Mapper;

import bolsoseguroapi.Dto.Categoria.CategoriaDTO;
import bolsoseguroapi.Model.Categoria;

public class CategoriaMapper {

    public static CategoriaDTO toDto(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNome(categoria.getNome());
        return dto;
    }

    public static Categoria toEntity(CategoriaDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setId(dto.getId());
        categoria.setNome(dto.getNome());
        return categoria;
    }
}
