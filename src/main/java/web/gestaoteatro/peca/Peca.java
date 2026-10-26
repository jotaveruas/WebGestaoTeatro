package web.gestaoteatro.peca;

import java.util.ArrayList;
import java.util.List;
import web.gestaoteatro.sessao.Sessao;

public class Peca {

    private Long codigo;
    private String nome;
    private List<Sessao> sessoes = new ArrayList<>();

    public Peca() {
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

    public List<Sessao> getSessoes() {
        return sessoes;
    }

    public void setSessoes(List<Sessao> sessoes) {
        this.sessoes = sessoes;
    }
}
