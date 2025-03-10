package bolsoseguroapi.Model;

import bolsoseguroapi.Model.Enum.UsuarioRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "conta_inicial_id")
    private Conta contaInicial;

    @Column(nullable = false)
    private BigDecimal saldoGeral = BigDecimal.ZERO;  // Saldo geral de todas as contas


    private String verificationCode;

    private boolean enabled;


    @OneToMany(mappedBy = "usuario")
    private List<Conta> contas; // Relacionamento com contas cadastradas

    @OneToMany(mappedBy = "usuario")
    private List<Cartao> cartoes; // Relacionamento com cartões cadastrados

    @OneToMany(mappedBy = "usuario")
    private List<Transacao> transacoes; // Relacionamento com transações realizadas

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Categoria> categorias;

    @Enumerated(EnumType.STRING)
    private UsuarioRole role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UsuarioRole.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));

    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }
}
