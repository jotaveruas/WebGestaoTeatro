package web.gestaoteatro.sessao.controller;

import java.time.LocalDate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import web.gestaoteatro.common.exception.RegraNegocioException;
import web.gestaoteatro.core.web.pagination.PageWrapper;
import web.gestaoteatro.sessao.dto.SessaoForm;
import web.gestaoteatro.sessao.service.SessaoService;

@Controller
@RequestMapping("/sessoes")
public class SessaoController {
    private final SessaoService service;

    public SessaoController(SessaoService service) { this.service = service; }

    @InitBinder
    void configurarFormulario(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.setAllowedFields("codigoPeca", "data", "horario", "local", "capacidade", "valor", "versao");
    }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "") String termo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "data") String ordem, Model model, HttpServletRequest request,
            HttpServletResponse response) {
        response.addHeader("Vary", "HX-Request, HX-History-Restore-Request");
        String busca = termo == null ? "" : termo;
        if (busca.length() > 100) busca = busca.substring(0, 100);
        String ordenacao = switch (ordem == null ? "data" : ordem) {
            case "dataDesc", "peca" -> ordem;
            default -> "data";
        };
        model.addAttribute("pagina", new PageWrapper<>(service.buscar(busca, data, pagina, ordenacao), request));
        model.addAttribute("termo", busca);
        model.addAttribute("dataFiltro", data);
        model.addAttribute("ordem", ordenacao);
        if ("true".equalsIgnoreCase(request.getHeader("HX-Request"))
                && !"true".equalsIgnoreCase(request.getHeader("HX-History-Restore-Request")))
            return "sessoes/resultado :: resultado";
        return "sessoes/lista";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("sessaoForm", new SessaoForm());
        return formulario(null, model);
    }

    @GetMapping("/{codigo}")
    public String detalhes(@PathVariable Long codigo, Model model) {
        model.addAttribute("sessao", service.buscarPorCodigo(codigo));
        return "sessoes/detalhes";
    }

    @GetMapping("/{codigo}/editar")
    public String editar(@PathVariable Long codigo, Model model) {
        model.addAttribute("sessaoForm", service.prepararEdicao(codigo));
        return formulario(codigo, model);
    }

    @PostMapping
    public String cadastrar(@Valid @ModelAttribute SessaoForm sessaoForm, BindingResult result,
            Model model, RedirectAttributes redirect, HttpServletResponse response) {
        return salvar(null, sessaoForm, result, model, redirect, response);
    }

    @PostMapping("/{codigo}")
    public String atualizar(@PathVariable Long codigo, @Valid @ModelAttribute SessaoForm sessaoForm,
            BindingResult result, Model model, RedirectAttributes redirect, HttpServletResponse response) {
        service.buscarPorCodigo(codigo);
        return salvar(codigo, sessaoForm, result, model, redirect, response);
    }

    private String salvar(Long codigo, SessaoForm form, BindingResult result, Model model,
            RedirectAttributes redirect, HttpServletResponse response) {
        if (result.hasErrors())
            return formulario(codigo, model);
        try {
            Long salvo = service.salvar(codigo, form);
            redirect.addFlashAttribute("sucesso", codigo == null ? "Sessão cadastrada." : "Sessão atualizada.");
            return "redirect:/sessoes/" + salvo;
        } catch (RegraNegocioException ex) {
            if (ex.getCampo() == null) result.reject("regra", ex.getMessage());
            else result.rejectValue(ex.getCampo(), "regra", ex.getMessage());
        } catch (OptimisticLockingFailureException ex) {
            response.setStatus(409);
            result.reject("conflito", "Esta sessão foi alterada por outra pessoa. Recarregue a página antes de salvar.");
        }
        return formulario(codigo, model);
    }

    @PostMapping("/{codigo}/excluir")
    public String excluir(@PathVariable Long codigo, RedirectAttributes redirect) {
        try {
            service.excluir(codigo);
            redirect.addFlashAttribute("sucesso", "Sessão excluída.");
        } catch (RegraNegocioException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
        } catch (DataIntegrityViolationException | OptimisticLockingFailureException ex) {
            redirect.addFlashAttribute("erro", "A sessão mudou ou recebeu um vínculo. Atualize a lista e tente novamente.");
        }
        return "redirect:/sessoes";
    }

    private String formulario(Long codigo, Model model) {
        model.addAttribute("codigo", codigo);
        model.addAttribute("pecas", service.listarPecas());
        return "sessoes/formulario";
    }
}
