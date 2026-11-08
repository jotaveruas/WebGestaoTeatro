package web.gestaoteatro.peca;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.hibernate.Hibernate;
import web.gestaoteatro.sessao.Sessao;

@Entity
@Table(name = "peca")
@Access(AccessType.FIELD)
public class Peca {

    @Id
    @SequenceGenerator(name = "peca_codigo_seq_gen", sequenceName = "peca_codigo_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "peca_codigo_seq_gen")
    private Long codigo;
    @Column(nullable = false, length = 160)
    private String nome;
    @Column(length = 4000)
    private String descricao;
    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos;
    @Column(name = "classificacao_etaria")
    private Integer classificacaoEtaria;
    @OneToMany(mappedBy = "peca", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("data ASC, horario ASC, codigo ASC")
    private List<Sessao> sessoes = new ArrayList<>();

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
        // equals reconhece a entidade mesmo quando o Hibernate usa um proxy.
        if (!sessoes.contains(sessao)) {
            sessoes.add(sessao);
        }
        if (!Objects.equals(sessao.getPeca(), this)) {
            sessao.setPeca(this);
        }
    }

    public void removeSessao(Sessao sessao) {
        if (sessao == null)
            return;
        sessoes.remove(sessao);
        if (Objects.equals(sessao.getPeca(), this)) {
            sessao.setPeca(null);
        }
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
        Peca other = (Peca) obj;
        return getCodigo() != null && Objects.equals(getCodigo(), other.getCodigo());
    }
}
