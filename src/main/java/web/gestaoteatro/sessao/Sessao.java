package web.gestaoteatro.sessao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
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
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.Hibernate;
import web.gestaoteatro.inscricao.Inscricao;
import web.gestaoteatro.peca.Peca;

@Entity
@Table(name = "sessao", indexes = @Index(name = "idx_sessao_peca", columnList = "codigo_peca"))
@Access(AccessType.FIELD)
public class Sessao {

    @Id
    @SequenceGenerator(name = "sessao_codigo_seq_gen", sequenceName = "sessao_codigo_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sessao_codigo_seq_gen")
    private Long codigo;

    @Version
    @Column(nullable = false)
    private Long versao;
    @Column(nullable = false)
    private LocalDate data;
    @Column(nullable = false)
    private LocalTime horario;
    @Column(nullable = false, length = 160)
    private String local;
    @Column(nullable = false)
    private Integer capacidade;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "codigo_peca", nullable = false)
    private Peca peca;
    @OneToMany(mappedBy = "sessao", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("dataInscricao ASC, codigo ASC")
    private List<Inscricao> inscricoes = new ArrayList<>();

    public Sessao() {
    }

    public Sessao(Long codigo, LocalDate data, LocalTime horario, String local,
            Integer capacidade, BigDecimal valor, Peca peca, List<Inscricao> inscricoes) {
        this.codigo = codigo;
        this.data = data;
        this.horario = horario;
        this.local = local;
        this.capacidade = capacidade;
        this.valor = valor;
        setInscricoes(inscricoes);
        setPeca(peca);
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Peca getPeca() {
        return peca;
    }

    public void setPeca(Peca peca) {
        if (Objects.equals(this.peca, peca))
            return;
        Peca anterior = this.peca;
        this.peca = peca;
        if (anterior != null) {
            anterior.removeSessao(this);
        }
        if (peca != null) {
            peca.addSessao(this);
        }
    }

    public List<Inscricao> getInscricoes() {
        return Collections.unmodifiableList(inscricoes);
    }

    public void setInscricoes(List<Inscricao> inscricoes) {
        Objects.requireNonNull(inscricoes, "A lista de inscricoes nao pode ser nula.");
        List<Inscricao> novos = new ArrayList<>(inscricoes);
        for (Inscricao item : novos) {
            Objects.requireNonNull(item, "A lista nao pode conter elementos nulos.");
        }
        for (Inscricao item : new ArrayList<>(this.inscricoes)) {
            removeInscricao(item);
        }
        for (Inscricao item : novos) {
            addInscricao(item);
        }
    }

    public void addInscricao(Inscricao inscricao) {
        Objects.requireNonNull(inscricao, "O relacionamento nao pode ser nulo.");
        // equals reconhece a entidade mesmo quando o Hibernate usa um proxy.
        if (!inscricoes.contains(inscricao)) {
            inscricoes.add(inscricao);
        }
        if (!Objects.equals(inscricao.getSessao(), this)) {
            inscricao.setSessao(this);
        }
    }

    public void removeInscricao(Inscricao inscricao) {
        if (inscricao == null)
            return;
        inscricoes.remove(inscricao);
        if (Objects.equals(inscricao.getSessao(), this)) {
            inscricao.setSessao(null);
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
        Sessao other = (Sessao) obj;
        return getCodigo() != null && Objects.equals(getCodigo(), other.getCodigo());
    }
}
