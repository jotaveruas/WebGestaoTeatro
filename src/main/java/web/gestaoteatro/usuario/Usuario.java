package web.gestaoteatro.usuario;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.Hibernate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import web.gestaoteatro.common.enums.Papel;

@Entity
@Table(name = "usuario")
@Access(AccessType.FIELD)
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario {

    @Id
    @SequenceGenerator(name = "usuario_codigo_seq_gen", sequenceName = "usuario_codigo_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_codigo_seq_gen")
    private Long codigo;
    @Column(nullable = false, length = 120)
    private String nome;
    @Column(nullable = false, unique = true, length = 60)
    private String login;
    @Column(nullable = false, unique = true, length = 254)
    private String email;
    @Column(nullable = false, length = 255)
    private String senha;
    @Column(nullable = false)
    private boolean ativo = true;
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "usuario_papel", joinColumns = @JoinColumn(name = "codigo_usuario"),
            uniqueConstraints = @UniqueConstraint(name = "uk_usuario_papel", columnNames = {"codigo_usuario", "papel"}))
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "papel", nullable = false, length = 20)
    private Set<Papel> papeis = EnumSet.noneOf(Papel.class);

    public Usuario() {
    }

    public Usuario(Long codigo, String nome, String login, String email,
            String senha, boolean ativo, Set<Papel> papeis) {
        this.codigo = codigo;
        this.nome = nome;
        this.login = login;
        this.email = email;
        this.senha = senha;
        this.ativo = ativo;
        setPapeis(papeis);
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Set<Papel> getPapeis() {
        return Collections.unmodifiableSet(papeis);
    }

    public void setPapeis(Set<Papel> papeis) {
        Objects.requireNonNull(papeis, "O conjunto de papeis nao pode ser nulo.");
        Set<Papel> novos = EnumSet.noneOf(Papel.class);
        for (Papel papel : papeis) {
            novos.add(Objects.requireNonNull(papel, "O papel nao pode ser nulo."));
        }
        this.papeis.clear();
        this.papeis.addAll(novos);
    }

    public void addPapel(Papel papel) {
        papeis.add(Objects.requireNonNull(papel, "O papel nao pode ser nulo."));
    }

    public void removePapel(Papel papel) {
        papeis.remove(papel);
    }

    public boolean possuiPapel(Papel papel) {
        return papeis.contains(papel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCodigo());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (Hibernate.getClass(this) != Hibernate.getClass(obj))
            return false;
        Usuario other = (Usuario) obj;
        return getCodigo() != null && Objects.equals(getCodigo(), other.getCodigo());
    }
}
