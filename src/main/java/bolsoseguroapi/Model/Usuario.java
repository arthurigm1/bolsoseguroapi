package bolsoseguroapi.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
