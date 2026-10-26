package web.gestaoteatro.sessao;

import java.util.ArrayList;
import java.util.List;
import web.gestaoteatro.inscricao.Inscricao;
import web.gestaoteatro.peca.Peca;

public class Sessao {

    private Long codigo;
    private Peca peca;
    private List<Inscricao> inscricoes = new ArrayList<>();

    public Sessao() {
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Peca getPeca() {
        return peca;
    }

    public void setPeca(Peca peca) {
        this.peca = peca;
    }

    public List<Inscricao> getInscricoes() {
        return inscricoes;
    }

    public void setInscricoes(List<Inscricao> inscricoes) {
        this.inscricoes = inscricoes;
    }
}
