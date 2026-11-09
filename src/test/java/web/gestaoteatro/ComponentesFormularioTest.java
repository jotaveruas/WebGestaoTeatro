package web.gestaoteatro;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import web.gestaoteatro.core.validation.StrongPassword;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(ComponentesFormularioTest.Configuracao.class)
class ComponentesFormularioTest {
    @Autowired MockMvc mvc;

    @Test
    void fragmentoSenhaNaoReenviaValorRecebidoNoModelo() throws Exception {
        String pagina = mvc.perform(get("/__teste/senha")).andExpect(status().isOk())
                .andExpect(content().string(not(containsString("SegredoApenasNoModelo!7"))))
                .andReturn().getResponse().getContentAsString();
        var html = Jsoup.parse(pagina);
        var input = html.selectFirst("input[name=senha]");
        assertNotNull(input);
        assertEquals("password", input.attr("type"));
        assertFalse(input.hasAttr("value"));
        assertTrue(html.select("input[type=hidden][name=senha]").isEmpty());
    }

    @Test
    void mostraErrosDeSenhaSemEcoarSenhaInvalida() throws Exception {
        String pagina = mvc.perform(post("/__teste/senha").param("senha", "curta"))
                .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("formSenha", "senha"))
                .andExpect(content().string(containsString("Tamanho mínimo de 8 caracteres")))
                .andExpect(content().string(not(containsString("curta"))))
                .andReturn().getResponse().getContentAsString();
        var input = Jsoup.parse(pagina).selectFirst("input[name=senha]");
        assertNotNull(input);
        assertEquals("true", input.attr("aria-invalid"));
        assertFalse(input.hasAttr("value"));
    }

    @Test
    void senhaValidaNaoVoltaNoHtml() throws Exception {
        mvc.perform(post("/__teste/senha").param("senha", "Teatro!x7M"))
                .andExpect(status().isOk()).andExpect(model().attributeHasNoErrors("formSenha"))
                .andExpect(content().string(not(containsString("Teatro!x7M"))));
    }

    @Test
    void senhaDesabilitadaNaoGeraCampoOculto() throws Exception {
        String pagina = mvc.perform(get("/__teste/senha").param("desabilitado", "true"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var html = Jsoup.parse(pagina);
        assertTrue(html.selectFirst("input[name=senha]").hasAttr("disabled"));
        assertTrue(html.selectFirst("button[aria-controls=senha]").hasAttr("disabled"));
        assertTrue(html.select("input[type=hidden][name=senha]").isEmpty());
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class Configuracao {
        @Bean SenhaController senhaControllerTeste() { return new SenhaController(); }
    }

    @TestComponent
    @Controller
    static class SenhaController {
        @GetMapping("/__teste/senha")
        String mostrar(@RequestParam(defaultValue = "false") boolean desabilitado, Model model) {
            FormSenha form = new FormSenha();
            form.setSenha("SegredoApenasNoModelo!7");
            model.addAttribute("formSenha", form);
            model.addAttribute("desabilitado", desabilitado);
            return "testes/senha";
        }

        @PostMapping("/__teste/senha")
        String validar(@Valid @ModelAttribute("formSenha") FormSenha form, BindingResult result, Model model) {
            model.addAttribute("desabilitado", false);
            return "testes/senha";
        }
    }

    public static class FormSenha {
        @NotBlank(message = "Informe a senha.")
        @StrongPassword(uppercase = 1, lowercase = 1, digits = 1, special = 1)
        private String senha;
        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }
    }
}
