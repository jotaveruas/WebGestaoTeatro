package web.gestaoteatro;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.stream.Collectors;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import web.gestaoteatro.core.validation.StrongPassword;

class StrongPasswordTest {
    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void iniciar() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void encerrar() { factory.close(); }

    @Test
    void aceitaSenhaValidaComRegrasDeCaracteres() {
        assertTrue(validator.validateValue(Estrita.class, "senha", "Teatro!x7M").isEmpty());
    }

    @Test
    void informaOsCriteriosVioladosEmPortugues() {
        String mensagem = mensagens(validator.validateValue(Estrita.class, "senha", "teatrolivre"));
        assertTrue(mensagem.contains("maiúsculo"));
        assertTrue(mensagem.contains("dígito"));
        assertTrue(mensagem.contains("especial"));
    }

    @Test
    void rejeitaSenhaCurtaComMensagemDinamica() {
        assertTrue(mensagens(validator.validateValue(Padrao.class, "senha", "curta")).contains("mínimo de 8"));
    }

    @Test
    void rejeitaSenhaAcimaDoLimite() {
        assertTrue(mensagens(validator.validateValue(Padrao.class, "senha", "x".repeat(33))).contains("máximo de 32"));
    }

    @Test
    void rejeitaSenhaComumSemDiferenciarMaiusculas() {
        String mensagem = mensagens(validator.validateValue(Padrao.class, "senha", "PASSWORD"));
        assertTrue(mensagem.contains("proibida ou comum"));
    }

    @Test
    void rejeitaSenhaAdicionalSemDiferenciarMaiusculas() {
        assertTrue(mensagens(validator.validateValue(Proibida.class, "senha", "TEATRO2026")).contains("proibida ou comum"));
    }

    @Test
    void rejeitaSequenciasConsecutivas() {
        assertFalse(validator.validateValue(SemSequencias.class, "senha", "abcX!8mN").isEmpty());
        assertFalse(validator.validateValue(SemSequencias.class, "senha", "123X!8mN").isEmpty());
    }

    @Test
    void rejeitaSequenciaDeTeclado() {
        assertTrue(mensagens(validator.validateValue(SemTeclado.class, "senha", "qweX!8mN")).contains("sequências"));
    }

    @Test
    void preservaMensagemPersonalizada() {
        assertEquals("Escolha outra senha.", mensagens(validator.validateValue(Personalizada.class, "senha", "curta")));
    }

    @Test
    void permiteNullQuandoPreenchimentoNaoForObrigatorio() {
        assertTrue(validator.validateValue(Padrao.class, "senha", null).isEmpty());
    }

    @Test
    void notBlankTornaPreenchimentoObrigatorio() {
        assertFalse(validator.validateValue(Obrigatoria.class, "senha", null).isEmpty());
        assertFalse(validator.validateValue(Obrigatoria.class, "senha", "        ").isEmpty());
    }

    @Test
    void permiteDesativarListaDeSenhasComuns() {
        assertTrue(validator.validateValue(SemLista.class, "senha", "password").isEmpty());
    }

    @Test
    void suportaRestricoesRepetidasNoMesmoCampo() {
        assertTrue(validator.validateValue(Repetida.class, "senha", "A7").isEmpty());
        assertFalse(validator.validateValue(Repetida.class, "senha", "AA").isEmpty());
    }

    private static String mensagens(Set<? extends ConstraintViolation<?>> violacoes) {
        return violacoes.stream().map(ConstraintViolation::getMessage).sorted().collect(Collectors.joining("; "));
    }

    static class Padrao { @StrongPassword String senha; }
    static class Estrita {
        @StrongPassword(uppercase = 1, lowercase = 1, digits = 1, special = 1) String senha;
    }
    static class Proibida { @StrongPassword(additionalForbidden = "Teatro2026") String senha; }
    static class SemSequencias { @StrongPassword(sequences = 3) String senha; }
    static class SemTeclado { @StrongPassword(keyboardSequences = 3) String senha; }
    static class Personalizada { @StrongPassword(message = "Escolha outra senha.") String senha; }
    static class Obrigatoria { @NotBlank @StrongPassword String senha; }
    static class SemLista { @StrongPassword(checkForbidden = false) String senha; }
    static class Repetida {
        @StrongPassword(minSize = 0, maxSize = 0, uppercase = 1, checkForbidden = false)
        @StrongPassword(minSize = 0, maxSize = 0, digits = 1, checkForbidden = false)
        String senha;
    }
}
