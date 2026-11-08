package web.gestaoteatro.inscricao;

import java.time.LocalDateTime;
import java.util.Objects;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.hibernate.Hibernate;
import web.gestaoteatro.common.enums.SituacaoInscricao;
import web.gestaoteatro.espectador.Espectador;
import web.gestaoteatro.sessao.Sessao;

@Entity
@Table(name = "inscricao", indexes = {
        @Index(name = "idx_inscricao_espectador", columnList = "codigo_espectador"),
        @Index(name = "idx_inscricao_sessao", columnList = "codigo_sessao")
})
@Access(AccessType.FIELD)
public class Inscricao {

    @Id
    @SequenceGenerator(name = "inscricao_codigo_seq_gen", sequenceName = "inscricao_codigo_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inscricao_codigo_seq_gen")
    private Long codigo;
    @Column(name = "data_inscricao", nullable = false)
    private LocalDateTime dataInscricao;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SituacaoInscricao situacao = SituacaoInscricao.PENDENTE;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "codigo_espectador", nullable = false)
    private Espectador espectador;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "codigo_sessao", nullable = false)
    private Sessao sessao;

    public Inscricao() {
    }

    public Inscricao(Long codigo, LocalDateTime dataInscricao, SituacaoInscricao situacao,
            Espectador espectador, Sessao sessao) {
        this.codigo = codigo;
        this.dataInscricao = dataInscricao;
        setSituacao(situacao);
        setEspectador(espectador);
        setSessao(sessao);
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public LocalDateTime getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(LocalDateTime dataInscricao) {
        this.dataInscricao = dataInscricao;
    }

    public SituacaoInscricao getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoInscricao situacao) {
        this.situacao = Objects.requireNonNull(situacao, "A situacao nao pode ser nula.");
    }

    public Espectador getEspectador() {
        return espectador;
    }

    public void setEspectador(Espectador espectador) {
        if (Objects.equals(this.espectador, espectador))
            return;
        Espectador anterior = this.espectador;
        this.espectador = espectador;
        if (anterior != null) {
            anterior.removeInscricao(this);
        }
        if (espectador != null) {
            espectador.addInscricao(this);
        }
    }

    public Sessao getSessao() {
        return sessao;
    }

    public void setSessao(Sessao sessao) {
        if (Objects.equals(this.sessao, sessao))
            return;
        Sessao anterior = this.sessao;
        this.sessao = sessao;
        if (anterior != null) {
            anterior.removeInscricao(this);
        }
        if (sessao != null) {
            sessao.addInscricao(this);
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
        Inscricao other = (Inscricao) obj;
        return getCodigo() != null && Objects.equals(getCodigo(), other.getCodigo());
    }
}
