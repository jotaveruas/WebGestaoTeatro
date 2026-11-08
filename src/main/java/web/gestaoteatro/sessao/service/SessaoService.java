package web.gestaoteatro.sessao.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.gestaoteatro.common.exception.EntidadeNaoEncontradaException;
import web.gestaoteatro.common.exception.RegraNegocioException;
import web.gestaoteatro.inscricao.repository.InscricaoRepository;
import web.gestaoteatro.peca.Peca;
import web.gestaoteatro.peca.repository.PecaRepository;
import web.gestaoteatro.sessao.Sessao;
import web.gestaoteatro.sessao.dto.SessaoDados;
import web.gestaoteatro.sessao.dto.SessaoForm;
import web.gestaoteatro.sessao.repository.SessaoRepository;

@Service
@Transactional(readOnly = true)
public class SessaoService {
    private final SessaoRepository sessoes;
    private final PecaRepository pecas;
    private final InscricaoRepository inscricoes;

    public SessaoService(SessaoRepository sessoes, PecaRepository pecas, InscricaoRepository inscricoes) {
        this.sessoes = sessoes;
        this.pecas = pecas;
        this.inscricoes = inscricoes;
    }

    public Page<SessaoDados> buscar(String termo, LocalDate data, int pagina, String ordem) {
        String texto = termo == null ? "" : termo.trim().toLowerCase(Locale.ROOT);
        Specification<Sessao> filtro = (root, query, builder) -> builder.conjunction();
        if (!texto.isEmpty()) {
            String busca = "%" + texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
            filtro = filtro.and((root, query, builder) -> builder.or(
                    builder.like(builder.lower(root.join("peca").get("nome")), busca, '\\'),
                    builder.like(builder.lower(root.get("local")), busca, '\\')));
        }
        if (data != null)
            filtro = filtro.and((root, query, builder) -> builder.equal(root.get("data"), data));
        Sort sort = switch (ordem) {
            case "dataDesc" -> Sort.by(Sort.Order.desc("data"), Sort.Order.desc("horario"), Sort.Order.desc("codigo"));
            case "peca" -> Sort.by("peca.nome", "data", "horario", "codigo");
            default -> Sort.by("data", "horario", "codigo");
        };
        Page<Sessao> resultado = sessoes.findAll(filtro, PageRequest.of(Math.max(0, pagina), 10, sort));
        if (resultado.getTotalPages() > 0 && resultado.getNumber() >= resultado.getTotalPages())
            resultado = sessoes.findAll(filtro, PageRequest.of(resultado.getTotalPages() - 1, 10, sort));
        return resultado.map(SessaoDados::de);
    }

    public SessaoDados buscarPorCodigo(Long codigo) {
        return SessaoDados.de(encontrar(codigo));
    }

    public List<Peca> listarPecas() {
        return pecas.findAll(Sort.by("nome", "codigo"));
    }

    public SessaoForm prepararEdicao(Long codigo) {
        SessaoDados sessao = buscarPorCodigo(codigo);
        SessaoForm form = new SessaoForm();
        form.setCodigoPeca(sessao.codigoPeca());
        form.setData(sessao.data());
        form.setHorario(sessao.horario());
        form.setLocal(sessao.local());
        form.setCapacidade(sessao.capacidade());
        form.setValor(sessao.valor());
        form.setVersao(sessao.versao());
        return form;
    }

    @Transactional
    public Long salvar(Long codigo, SessaoForm form) {
        Sessao sessao = codigo == null ? new Sessao() : encontrar(codigo);
        if (codigo != null && !Objects.equals(sessao.getVersao(), form.getVersao()))
            throw new ObjectOptimisticLockingFailureException(Sessao.class, codigo);
        Peca peca = pecas.findById(form.getCodigoPeca()).orElseThrow(() ->
                new RegraNegocioException("codigoPeca", "A peça selecionada não está mais disponível."));
        sessao.setPeca(peca);
        sessao.setData(form.getData());
        sessao.setHorario(form.getHorario());
        sessao.setLocal(form.getLocal().trim());
        sessao.setCapacidade(form.getCapacidade());
        sessao.setValor(form.getValor());
        return sessoes.saveAndFlush(sessao).getCodigo();
    }

    @Transactional
    public void excluir(Long codigo) {
        Sessao sessao = encontrar(codigo);
        if (inscricoes.existsBySessaoCodigo(codigo))
            throw new RegraNegocioException("Esta sessão possui inscrições e não pode ser excluída.");
        sessoes.delete(sessao);
        sessoes.flush();
    }

    private Sessao encontrar(Long codigo) {
        return sessoes.findById(codigo).orElseThrow(() ->
                new EntidadeNaoEncontradaException("Sessão não encontrada."));
    }
}
