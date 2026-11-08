package web.gestaoteatro;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumSet;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import web.gestaoteatro.common.enums.Papel;
import web.gestaoteatro.common.enums.SituacaoInscricao;
import web.gestaoteatro.core.web.pagination.PageWrapper;
import web.gestaoteatro.espectador.Espectador;
import web.gestaoteatro.inscricao.Inscricao;
import web.gestaoteatro.peca.Peca;
import web.gestaoteatro.peca.repository.PecaRepository;
import web.gestaoteatro.sessao.Sessao;
import web.gestaoteatro.sessao.repository.SessaoRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SessaoCrudTest {
    @Autowired MockMvc mvc;
    @Autowired SessaoRepository sessoes;
    @Autowired PecaRepository pecas;
    @PersistenceContext EntityManager manager;

    @Test
    void inicioRedirecionaParaSessoes() throws Exception {
        mvc.perform(get("/")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/sessoes"));
    }

    @Test
    void listaVaziaEFormularioSaoRenderizados() throws Exception {
        mvc.perform(get("/sessoes")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Nenhuma sessão encontrada")));
        mvc.perform(get("/sessoes/nova")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Hamlet")))
                .andExpect(content().string(containsString("Cadastrar sessão")));
    }

    @Test
    void cadastraConsultaEditaEExcluiPeloController() throws Exception {
        mvc.perform(formulario(post("/sessoes"))).andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("sucesso", "Sessão cadastrada."));
        Sessao criada = sessoes.findAll().getFirst();
        Long codigo = criada.getCodigo();
        mvc.perform(get("/sessoes/{id}", codigo)).andExpect(status().isOk())
                .andExpect(content().string(containsString("Teatro Municipal")));
        mvc.perform(get("/sessoes/{id}/editar", codigo)).andExpect(status().isOk())
                .andExpect(content().string(containsString("Salvar alterações")));
        mvc.perform(formulario(post("/sessoes/{id}", codigo)).param("versao", criada.getVersao().toString())
                        .with(request -> { request.setParameter("local", "Teatro Central"); return request; }))
                .andExpect(status().is3xxRedirection());
        manager.clear();
        assertEquals("Teatro Central", sessoes.findById(codigo).orElseThrow().getLocal());
        mvc.perform(post("/sessoes/{id}/excluir", codigo)).andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("sucesso", "Sessão excluída."));
        assertFalse(sessoes.existsById(codigo));
    }

    @Test
    void formularioVazioMostraErrosSemGravar() throws Exception {
        mvc.perform(post("/sessoes")).andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("sessaoForm", "codigoPeca", "data", "horario", "local", "capacidade", "valor"))
                .andExpect(content().string(containsString("Informe o local.")));
        assertEquals(0, sessoes.count());
    }

    @Test
    void rejeitaValoresInvalidosELocalMuitoLongo() throws Exception {
        mvc.perform(formulario(post("/sessoes")).with(request -> {
            request.setParameter("capacidade", "0");
            request.setParameter("valor", "-1.001");
            request.setParameter("local", "X".repeat(161));
            return request;
        }))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("sessaoForm", "capacidade", "valor", "local"));
        assertEquals(0, sessoes.count());
    }

    @Test
    void rejeitaTiposInvalidosSemRetornarErroInterno() throws Exception {
        mvc.perform(formulario(post("/sessoes")).with(request -> {
            request.setParameter("data", "invalida");
            request.setParameter("capacidade", "abc");
            request.setParameter("valor", "abc");
            return request;
        }))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("sessaoForm", "data", "capacidade", "valor"));
        assertEquals(0, sessoes.count());
    }

    @Test
    void rejeitaPecaInexistenteNoFormulario() throws Exception {
        mvc.perform(formulario(post("/sessoes")).with(request -> {
            request.setParameter("codigoPeca", "999999"); return request;
        }))
                .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("sessaoForm", "codigoPeca"));
        assertEquals(0, sessoes.count());
    }

    @Test
    void sessaoInexistenteRetornaPagina404() throws Exception {
        mvc.perform(get("/sessoes/999999")).andExpect(status().isNotFound()).andExpect(view().name("error/404"));
        mvc.perform(get("/sessoes/999999/editar")).andExpect(status().isNotFound());
        mvc.perform(formulario(post("/sessoes/999999"))).andExpect(status().isNotFound());
    }

    @Test
    void preservaSessaoQuePossuiInscricoes() throws Exception {
        Sessao sessao = novaSessao("Teatro Municipal");
        Espectador espectador = new Espectador();
        espectador.setNome("Ana");
        espectador.setLogin("ana");
        espectador.setEmail("ana@example.com");
        espectador.setSenha("hash-apenas-teste");
        espectador.setPapeis(EnumSet.of(Papel.ESPECTADOR));
        manager.persist(espectador);
        manager.persist(new Inscricao(null, LocalDateTime.now(), SituacaoInscricao.CONFIRMADA, espectador, sessao));
        manager.flush();
        mvc.perform(post("/sessoes/{id}/excluir", sessao.getCodigo())).andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("erro", containsString("possui inscrições")));
        assertTrue(sessoes.existsById(sessao.getCodigo()));
    }

    @Test
    void buscaPorLocalSemDiferenciarMaiusculasECombinaData() throws Exception {
        Sessao encontrada = novaSessao("Teatro Municipal");
        novaSessao("Auditório");
        mvc.perform(get("/sessoes").param("termo", "MUNICIPAL").param("data", encontrada.getData().toString()))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Teatro Municipal")))
                .andExpect(content().string(not(containsString("Auditório"))));
    }

    @Test
    void buscaTrataPercentualESublinhadoComoTextoLiteral() throws Exception {
        novaSessao("Sala 10%_A");
        novaSessao("Sala 100AA");
        mvc.perform(get("/sessoes").param("termo", "%_")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Sala 10%_A")))
                .andExpect(content().string(not(containsString("Sala 100AA"))));
    }

    @Test
    void paginaResultadosENormalizaPaginaForaDoIntervalo() throws Exception {
        for (int i = 0; i < 12; i++) novaSessao("Sala " + i);
        var resultado = mvc.perform(get("/sessoes").param("pagina", "1")).andExpect(status().isOk()).andReturn();
        var pagina = (PageWrapper<?>) resultado.getModelAndView().getModel().get("pagina");
        assertEquals(12, pagina.getTotalElementos());
        assertEquals(2, pagina.getConteudo().size());
        resultado = mvc.perform(get("/sessoes").param("pagina", "999999")).andExpect(status().isOk()).andReturn();
        pagina = (PageWrapper<?>) resultado.getModelAndView().getModel().get("pagina");
        assertEquals(1, pagina.getAtual());
        assertEquals(2, pagina.getConteudo().size());
    }

    @Test
    void htmxRecebeFragmentoEHistoricoRecebePaginaInteira() throws Exception {
        mvc.perform(get("/sessoes").header("HX-Request", "true")).andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"resultado-sessoes\"")))
                .andExpect(content().string(not(containsString("<html"))));
        mvc.perform(get("/sessoes").header("HX-Request", "true").header("HX-History-Restore-Request", "true"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("<html")));
    }

    @Test
    void rejeitaBuscaComDataInvalida() throws Exception {
        mvc.perform(get("/sessoes").param("data", "invalida")).andExpect(status().isBadRequest()).andExpect(view().name("error/400"));
    }

    @Test
    void escapaTextoDigitadoPeloUsuario() throws Exception {
        Sessao sessao = novaSessao("<script>alert(1)</script>");
        mvc.perform(get("/sessoes/{id}", sessao.getCodigo())).andExpect(status().isOk())
                .andExpect(content().string(containsString("&lt;script&gt;")))
                .andExpect(content().string(not(containsString("<script>alert(1)</script>"))));
    }

    @Test
    void impedeSobrescreverEdicaoMaisRecente() throws Exception {
        Sessao sessao = novaSessao("Original");
        Long codigo = sessao.getCodigo();
        Long versaoAntiga = sessao.getVersao();
        sessao.setLocal("Editado por outra pessoa");
        sessoes.flush();
        mvc.perform(formulario(post("/sessoes/{id}", codigo)).param("versao", versaoAntiga.toString()))
                .andExpect(status().isConflict()).andExpect(model().attributeHasErrors("sessaoForm"))
                .andExpect(content().string(containsString("Recarregar dados atuais")));
        assertEquals("Editado por outra pessoa", sessoes.findById(codigo).orElseThrow().getLocal());
    }

    @Test
    void naoPermiteEscolherCodigoDaNovaSessao() throws Exception {
        mvc.perform(formulario(post("/sessoes")).param("codigo", "999999"))
                .andExpect(status().is3xxRedirection());
        assertEquals(1, sessoes.count());
        assertNotEquals(999999L, sessoes.findAll().getFirst().getCodigo());
    }

    private MockHttpServletRequestBuilder formulario(MockHttpServletRequestBuilder request) {
        return request.param("codigoPeca", peca().getCodigo().toString())
                .param("data", "2026-12-12").param("horario", "20:00")
                .param("local", "Teatro Municipal").param("capacidade", "100").param("valor", "45.90");
    }

    private Peca peca() { return pecas.findAll().getFirst(); }

    private Sessao novaSessao(String local) {
        Sessao sessao = new Sessao();
        sessao.setPeca(peca());
        sessao.setData(LocalDate.of(2026, 12, 12));
        sessao.setHorario(LocalTime.of(20, 0));
        sessao.setLocal(local);
        sessao.setCapacidade(100);
        sessao.setValor(new BigDecimal("45.90"));
        return sessoes.saveAndFlush(sessao);
    }
}
