package web.gestaoteatro.inscricao;

import java.time.LocalDateTime;
import java.util.Objects;
import web.gestaoteatro.common.enums.SituacaoInscricao;
import web.gestaoteatro.espectador.Espectador;
import web.gestaoteatro.sessao.Sessao;

public class Inscricao {

    private Long codigo;
    private LocalDateTime dataInscricao;
    private SituacaoInscricao situacao = SituacaoInscricao.PENDENTE;
    private Espectador espectador;
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
        if (this.espectador == espectador)
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
        if (this.sessao == sessao)
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
        Inscricao other = (Inscricao) obj;
        return codigo != null && Objects.equals(codigo, other.codigo);
    }
}
