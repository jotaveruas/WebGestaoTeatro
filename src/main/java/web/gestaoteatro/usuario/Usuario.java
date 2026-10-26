package web.gestaoteatro.usuario;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import web.gestaoteatro.common.enums.Papel;

public class Usuario {

    private Long codigo;
    private String nome;
    private String login;
    private String email;
    private String senha;
    private boolean ativo = true;
    private final Set<Papel> papeis = EnumSet.noneOf(Papel.class);

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
        return Objects.hash(codigo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Usuario other = (Usuario) obj;
        return codigo != null && Objects.equals(codigo, other.codigo);
    }
}
