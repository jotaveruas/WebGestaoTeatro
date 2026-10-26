package web.gestaoteatro.peca;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import web.gestaoteatro.sessao.Sessao;

public class Peca {

    private Long codigo;
    private String nome;
    private String descricao;
    private Integer duracaoMinutos;
    private Integer classificacaoEtaria;
    private final List<Sessao> sessoes = new ArrayList<>();

    public Peca() {
    }

    public Peca(Long codigo, String nome, String descricao, Integer duracaoMinutos,
            Integer classificacaoEtaria, List<Sessao> sessoes) {
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.duracaoMinutos = duracaoMinutos;
        this.classificacaoEtaria = classificacaoEtaria;
        setSessoes(sessoes);
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(Integer duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public Integer getClassificacaoEtaria() {
        return classificacaoEtaria;
    }

    public void setClassificacaoEtaria(Integer classificacaoEtaria) {
        this.classificacaoEtaria = classificacaoEtaria;
    }

    public List<Sessao> getSessoes() {
        return Collections.unmodifiableList(sessoes);
    }

    public void setSessoes(List<Sessao> sessoes) {
        Objects.requireNonNull(sessoes, "A lista de sessoes nao pode ser nula.");
        List<Sessao> novos = new ArrayList<>(sessoes);
        for (Sessao item : novos) {
            Objects.requireNonNull(item, "A lista nao pode conter elementos nulos.");
        }
        for (Sessao item : new ArrayList<>(this.sessoes)) {
            removeSessao(item);
        }
        for (Sessao item : novos) {
            addSessao(item);
        }
    }

    public void addSessao(Sessao sessao) {
        Objects.requireNonNull(sessao, "O relacionamento nao pode ser nulo.");
        // O vinculo usa a instancia: codigo pode mudar e objetos novos nao tem ID.
        if (sessoes.stream().noneMatch(item -> item == sessao)) {
            sessoes.add(sessao);
        }
        if (sessao.getPeca() != this) {
            sessao.setPeca(this);
        }
    }

    public void removeSessao(Sessao sessao) {
        if (sessao == null)
            return;
        sessoes.removeIf(item -> item == sessao);
        if (sessao.getPeca() == this) {
            sessao.setPeca(null);
        }
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
        Peca other = (Peca) obj;
        return codigo != null && Objects.equals(codigo, other.codigo);
    }
}
