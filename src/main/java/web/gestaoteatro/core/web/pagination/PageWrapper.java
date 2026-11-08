package web.gestaoteatro.core.web.pagination;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.util.UriComponentsBuilder;

// Adaptado do PageWrapper disponibilizado na Parte 04 da disciplina.
public class PageWrapper<T> {
    private final Page<T> pagina;
    private final UriComponentsBuilder uri;

    public PageWrapper(Page<T> pagina, HttpServletRequest request) {
        this.pagina = pagina;
        String query = request.getQueryString();
        this.uri = UriComponentsBuilder.fromUriString(request.getRequestURI()
                + (query == null || query.isBlank() ? "" : "?" + query));
    }

    public List<T> getConteudo() { return pagina.getContent(); }
    public long getTotalElementos() { return pagina.getTotalElements(); }
    public int getAtual() { return pagina.getNumber(); }
    public int getNumeroPaginas() { return pagina.getTotalPages(); }
    public boolean isVazia() { return pagina.isEmpty(); }
    public boolean isPrimeira() { return pagina.isFirst(); }
    public boolean isUltima() { return pagina.isLast(); }
    public int getInicio() { return Math.max(0, Math.min(getAtual() - 2, getNumeroPaginas() - 5)); }
    public int getFim() { return Math.min(getNumeroPaginas() - 1, getInicio() + 4); }

    public String urlParaPagina(int numero) {
        return uri.cloneBuilder().replaceQueryParam("pagina", Math.max(0, numero)).build(true).toUriString();
    }
}
