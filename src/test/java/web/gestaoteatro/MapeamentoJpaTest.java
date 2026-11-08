package web.gestaoteatro;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.EnumSet;
import jakarta.persistence.PersistenceException;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import web.gestaoteatro.common.enums.Papel;
import web.gestaoteatro.common.enums.SituacaoInscricao;
import web.gestaoteatro.espectador.Espectador;
import web.gestaoteatro.inscricao.Inscricao;
import web.gestaoteatro.peca.Peca;
import web.gestaoteatro.sessao.Sessao;
import web.gestaoteatro.usuario.Usuario;

class MapeamentoJpaTest {

    private static SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    @BeforeAll
    static void configurarBancoDeTeste() {
        sessionFactory = new Configuration()
                .addAnnotatedClass(Usuario.class)
                .addAnnotatedClass(Espectador.class)
                .addAnnotatedClass(Peca.class)
                .addAnnotatedClass(Sessao.class)
                .addAnnotatedClass(Inscricao.class)
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty("hibernate.connection.url", "jdbc:h2:mem:teatro;MODE=PostgreSQL;DB_CLOSE_DELAY=-1")
                .setProperty("hibernate.connection.username", "sa")
                .setProperty("hibernate.connection.password", "")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .setProperty("hibernate.show_sql", "false")
                .buildSessionFactory();
    }

    @BeforeEach
    void iniciarTransacao() {
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
    }

    @AfterEach
    void desfazerTransacao() {
        if (transaction != null && transaction.isActive())
            transaction.rollback();
        if (session != null)
            session.close();
    }

    @AfterAll
    static void fecharBancoDeTeste() {
        if (sessionFactory != null)
            sessionFactory.close();
    }

    @Test
    void gravaERecarregaHerancaRelacionamentosValoresEEnums() {
        Espectador espectador = novoEspectador();
        espectador.addPapel(Papel.FUNCIONARIO);
        Peca peca = novaPeca("Hamlet");
        Sessao sessao = novaSessao(peca, LocalDate.of(2026, 11, 12));
        Sessao primeira = novaSessao(peca, LocalDate.of(2026, 11, 10));
        Inscricao inscricao = new Inscricao(null, LocalDateTime.of(2026, 11, 7, 18, 30),
                SituacaoInscricao.CONFIRMADA, espectador, sessao);
        session.persist(espectador);
        session.persist(peca);
        session.flush();

        Long codigoPeca = peca.getCodigo();
        Long codigoEspectador = espectador.getCodigo();
        Long codigoInscricao = inscricao.getCodigo();
        assertNotNull(primeira.getCodigo());
        session.clear();

        Peca encontrada = session.find(Peca.class, codigoPeca);
        assertEquals(2, encontrada.getSessoes().size());
        assertEquals(LocalDate.of(2026, 11, 10), encontrada.getSessoes().get(0).getData());
        assertEquals(1, encontrada.getSessoes().get(1).getInscricoes().size());
        assertEquals(new BigDecimal("45.90"), encontrada.getSessoes().get(1).getValor());

        Usuario usuario = session.find(Usuario.class, codigoEspectador);
        assertInstanceOf(Espectador.class, usuario);
        Espectador encontrado = (Espectador) usuario;
        assertEquals(codigoEspectador, encontrado.getCodigo());
        assertEquals("Ana", encontrado.getNome());
        assertEquals(EnumSet.of(Papel.ESPECTADOR, Papel.FUNCIONARIO), encontrado.getPapeis());
        assertEquals(1, encontrado.getInscricoes().size());

        Inscricao encontradaInscricao = session.find(Inscricao.class, codigoInscricao);
        assertEquals(SituacaoInscricao.CONFIRMADA, encontradaInscricao.getSituacao());
        assertEquals(LocalDateTime.of(2026, 11, 7, 18, 30), encontradaInscricao.getDataInscricao());
        assertEquals("CONFIRMADA", session.createNativeQuery(
                "select situacao from inscricao where codigo = :codigo", String.class)
                .setParameter("codigo", codigoInscricao).getSingleResult());
        assertEquals(1L, session.createNativeQuery(
                "select count(*) from usuario where codigo = :codigo", Long.class)
                .setParameter("codigo", codigoEspectador).getSingleResult());
        assertEquals(1L, session.createNativeQuery(
                "select count(*) from espectador where codigo = :codigo", Long.class)
                .setParameter("codigo", codigoEspectador).getSingleResult());
    }

    @Test
    void carregaPecaSobDemanda() {
        Peca peca = novaPeca("A Tempestade");
        Sessao sessao = novaSessao(peca, LocalDate.of(2026, 11, 10));
        session.persist(peca);
        session.flush();
        Long codigoSessao = sessao.getCodigo();
        session.clear();

        Sessao encontrada = session.find(Sessao.class, codigoSessao);
        assertFalse(Hibernate.isInitialized(encontrada.getPeca()));
        assertEquals("A Tempestade", encontrada.getPeca().getNome());
        assertTrue(Hibernate.isInitialized(encontrada.getPeca()));
    }

    @Test
    void transfereSessaoMesmoQuandoAPecaEUmProxy() {
        Peca antiga = novaPeca("Peca antiga");
        Peca nova = novaPeca("Peca nova");
        Sessao sessao = novaSessao(antiga, LocalDate.of(2026, 11, 10));
        session.persist(antiga);
        session.persist(nova);
        session.flush();
        Long codigoAntiga = antiga.getCodigo();
        Long codigoNova = nova.getCodigo();
        Long codigoSessao = sessao.getCodigo();
        session.clear();

        Sessao encontrada = session.find(Sessao.class, codigoSessao);
        Peca proxy = session.getReference(Peca.class, codigoNova);
        proxy.addSessao(encontrada);
        session.flush();
        session.clear();

        assertTrue(session.find(Peca.class, codigoAntiga).getSessoes().isEmpty());
        assertEquals(1, session.find(Peca.class, codigoNova).getSessoes().size());
        assertEquals(codigoNova, session.find(Sessao.class, codigoSessao).getPeca().getCodigo());
    }

    @Test
    void atualizaColecaoDePapeisSemSubstituirColecaoGerenciada() {
        Espectador espectador = novoEspectador();
        session.persist(espectador);
        session.flush();
        Long codigo = espectador.getCodigo();
        session.clear();

        Espectador encontrado = session.find(Espectador.class, codigo);
        encontrado.setPapeis(encontrado.getPapeis());
        encontrado.addPapel(Papel.ADMIN);
        session.flush();
        session.clear();

        assertEquals(EnumSet.of(Papel.ESPECTADOR, Papel.ADMIN),
                session.find(Espectador.class, codigo).getPapeis());
        assertEquals(2, session.createNativeQuery(
                "select papel from usuario_papel where codigo_usuario = :codigo", String.class)
                .setParameter("codigo", codigo).getResultList().size());
    }

    @Test
    void reconheceIdentidadeDeEntidadesEProxies() {
        assertNotEquals(new Peca(), new Peca());
        Peca peca = novaPeca("Identidade");
        Espectador espectador = novoEspectador();
        session.persist(peca);
        session.persist(espectador);
        session.flush();
        Long codigo = peca.getCodigo();
        Long codigoEspectador = espectador.getCodigo();
        session.clear();

        Peca proxy = session.getReference(Peca.class, codigo);
        assertEquals(peca, proxy);
        assertEquals(proxy, peca);
        assertEquals(peca.hashCode(), proxy.hashCode());
        Usuario proxyUsuario = session.getReference(Usuario.class, codigoEspectador);
        assertEquals(espectador, proxyUsuario);
        assertEquals(proxyUsuario, espectador);
        assertEquals(espectador.hashCode(), proxyUsuario.hashCode());
    }

    @Test
    void impedeExcluirPecaQuePossuiSessoes() {
        Peca peca = novaPeca("Peca com sessao");
        novaSessao(peca, LocalDate.of(2026, 11, 10));
        session.persist(peca);
        session.flush();
        Long codigo = peca.getCodigo();
        session.clear();
        session.remove(session.find(Peca.class, codigo));
        assertThrows(PersistenceException.class, () -> session.flush());
    }

    @Test
    void transfereInscricaoComEspectadorESessaoCarregadosComoProxies() {
        Espectador primeiro = novoEspectador();
        Espectador segundo = novoEspectador();
        segundo.setLogin("ana2");
        segundo.setEmail("ana2@example.com");
        segundo.setCpf("98765432100");
        Peca peca = novaPeca("Transferencia de inscricao");
        Sessao primeira = novaSessao(peca, LocalDate.of(2026, 11, 10));
        Sessao segunda = novaSessao(peca, LocalDate.of(2026, 11, 12));
        Inscricao inscricao = new Inscricao(null, LocalDateTime.of(2026, 11, 7, 18, 30),
                SituacaoInscricao.PENDENTE, primeiro, primeira);
        session.persist(primeiro);
        session.persist(segundo);
        session.persist(peca);
        session.flush();
        Long codigoInscricao = inscricao.getCodigo();
        Long codigoPrimeiro = primeiro.getCodigo();
        Long codigoSegundo = segundo.getCodigo();
        Long codigoPrimeira = primeira.getCodigo();
        Long codigoSegunda = segunda.getCodigo();
        session.clear();

        Inscricao encontrada = session.find(Inscricao.class, codigoInscricao);
        session.getReference(Espectador.class, codigoSegundo).addInscricao(encontrada);
        session.getReference(Sessao.class, codigoSegunda).addInscricao(encontrada);
        session.flush();
        session.clear();

        assertTrue(session.find(Espectador.class, codigoPrimeiro).getInscricoes().isEmpty());
        assertTrue(session.find(Sessao.class, codigoPrimeira).getInscricoes().isEmpty());
        assertEquals(1, session.find(Espectador.class, codigoSegundo).getInscricoes().size());
        assertEquals(1, session.find(Sessao.class, codigoSegunda).getInscricoes().size());
        assertEquals(codigoSegundo, session.find(Inscricao.class, codigoInscricao).getEspectador().getCodigo());
        assertEquals(codigoSegunda, session.find(Inscricao.class, codigoInscricao).getSessao().getCodigo());
    }

    @Test
    void impedeLoginDuplicadoNoBanco() {
        session.persist(new Usuario(null, "Ana", "ana", "ana@example.com", "hash-exemplo",
                true, EnumSet.of(Papel.ADMIN)));
        session.persist(new Usuario(null, "Outra Ana", "ana", "outra@example.com", "hash-exemplo",
                true, EnumSet.of(Papel.FUNCIONARIO)));
        assertThrows(PersistenceException.class, () -> session.flush());
    }

    @Test
    void exigePecaParaPersistirUmaSessao() {
        Sessao sessao = novaSessao(null, LocalDate.of(2026, 11, 10));
        assertThrows(PersistenceException.class, () -> {
            session.persist(sessao);
            session.flush();
        });
    }

    private Espectador novoEspectador() {
        return new Espectador(null, "Ana", "ana", "ana@example.com", "hash-exemplo", true,
                EnumSet.of(Papel.ESPECTADOR), "12345678909", "34999999999",
                LocalDate.of(2000, 1, 1), Collections.emptyList());
    }

    private Peca novaPeca(String nome) {
        return new Peca(null, nome, "Descricao da peca", 120, 12, Collections.emptyList());
    }

    private Sessao novaSessao(Peca peca, LocalDate data) {
        return new Sessao(null, data, LocalTime.of(20, 0), "Teatro Municipal", 100,
                new BigDecimal("45.90"), peca, Collections.emptyList());
    }
}
