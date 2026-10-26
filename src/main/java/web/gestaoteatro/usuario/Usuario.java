package web.gestaoteatro.usuario;

import java.util.EnumSet;
import java.util.Set;
import web.gestaoteatro.common.enums.Papel;

public class Usuario {

    private Long codigo;
    private String nome;
    private Set<Papel> papeis = EnumSet.noneOf(Papel.class);

    public Usuario() {
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

    public Set<Papel> getPapeis() {
        return papeis;
    }

    public void setPapeis(Set<Papel> papeis) {
        this.papeis = papeis;
    }
}
