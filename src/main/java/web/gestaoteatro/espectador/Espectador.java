package web.gestaoteatro.espectador;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import web.gestaoteatro.common.enums.Papel;
import web.gestaoteatro.inscricao.Inscricao;
import web.gestaoteatro.usuario.Usuario;

@Entity
@Table(name = "espectador")
@Access(AccessType.FIELD)
@PrimaryKeyJoinColumn(name = "codigo")
public class Espectador extends Usuario {

    @Column(unique = true, length = 11)
    private String cpf;
    @Column(length = 20)
    private String telefone;
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;
    @OneToMany(mappedBy = "espectador", fetch = FetchType.LAZY)
    @OrderBy("dataInscricao ASC, codigo ASC")
    private List<Inscricao> inscricoes = new ArrayList<>();

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
        // equals reconhece a entidade mesmo quando o Hibernate usa um proxy.
        if (!inscricoes.contains(inscricao)) {
            inscricoes.add(inscricao);
        }
        if (!Objects.equals(inscricao.getEspectador(), this)) {
            inscricao.setEspectador(this);
        }
    }

    public void removeInscricao(Inscricao inscricao) {
        if (inscricao == null)
            return;
        inscricoes.remove(inscricao);
        if (Objects.equals(inscricao.getEspectador(), this)) {
            inscricao.setEspectador(null);
        }
    }
}
