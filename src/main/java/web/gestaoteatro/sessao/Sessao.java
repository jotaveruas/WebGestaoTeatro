package web.gestaoteatro.sessao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import web.gestaoteatro.inscricao.Inscricao;
import web.gestaoteatro.peca.Peca;

public class Sessao {

    private Long codigo;
    private LocalDate data;
    private LocalTime horario;
    private String local;
    private Integer capacidade;
    private BigDecimal valor;
    private Peca peca;
    private final List<Inscricao> inscricoes = new ArrayList<>();

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
        if (this.peca == peca)
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
        // O vinculo usa a instancia: codigo pode mudar e objetos novos nao tem ID.
        if (inscricoes.stream().noneMatch(item -> item == inscricao)) {
            inscricoes.add(inscricao);
        }
        if (inscricao.getSessao() != this) {
            inscricao.setSessao(this);
        }
    }

    public void removeInscricao(Inscricao inscricao) {
        if (inscricao == null)
            return;
        inscricoes.removeIf(item -> item == inscricao);
        if (inscricao.getSessao() == this) {
            inscricao.setSessao(null);
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
        Sessao other = (Sessao) obj;
        return codigo != null && Objects.equals(codigo, other.codigo);
    }
}
