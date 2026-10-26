package web.gestaoteatro.espectador;

import java.util.ArrayList;
import java.util.List;
import web.gestaoteatro.inscricao.Inscricao;
import web.gestaoteatro.usuario.Usuario;

public class Espectador extends Usuario {

    private List<Inscricao> inscricoes = new ArrayList<>();

    public Espectador() {
    }

    public List<Inscricao> getInscricoes() {
        return inscricoes;
    }

    public void setInscricoes(List<Inscricao> inscricoes) {
        this.inscricoes = inscricoes;
    }
}
