package web.gestaoteatro.espectador;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import web.gestaoteatro.common.enums.Papel;
import web.gestaoteatro.inscricao.Inscricao;
import web.gestaoteatro.usuario.Usuario;

public class Espectador extends Usuario {

    private String cpf;
    private String telefone;
    private LocalDate dataNascimento;
    private final List<Inscricao> inscricoes = new ArrayList<>();

    public Espectador() {
        addPapel(Papel.ESPECTADOR);
    }

    public Espectador(Long codigo, String nome, String login, String email,
            String senha, boolean ativo, Set<Papel> papeis, String cpf,
            String telefone, LocalDate dataNascimento, List<Inscricao> inscricoes) {
        super(codigo, nome, login, email, senha, ativo, papeis);
        addPapel(Papel.ESPECTADOR);
        this.cpf = cpf;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
        setInscricoes(inscricoes);
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
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
        if (inscricao.getEspectador() != this) {
            inscricao.setEspectador(this);
        }
    }

    public void removeInscricao(Inscricao inscricao) {
        if (inscricao == null)
            return;
        inscricoes.removeIf(item -> item == inscricao);
        if (inscricao.getEspectador() == this) {
            inscricao.setEspectador(null);
        }
    }
}
