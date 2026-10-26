package web.gestaoteatro.inscricao;

import web.gestaoteatro.espectador.Espectador;
import web.gestaoteatro.sessao.Sessao;

public class Inscricao {

    private Long codigo;
    private Espectador espectador;
    private Sessao sessao;

    public Inscricao() {
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Espectador getEspectador() {
        return espectador;
    }

    public void setEspectador(Espectador espectador) {
        this.espectador = espectador;
    }

    public Sessao getSessao() {
        return sessao;
    }

    public void setSessao(Sessao sessao) {
        this.sessao = sessao;
    }
}
