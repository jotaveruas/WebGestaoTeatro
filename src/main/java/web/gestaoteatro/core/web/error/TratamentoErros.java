package web.gestaoteatro.core.web.error;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import web.gestaoteatro.common.exception.EntidadeNaoEncontradaException;

@ControllerAdvice
public class TratamentoErros {
    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String naoEncontrada(EntidadeNaoEncontradaException ex, Model model) {
        model.addAttribute("mensagem", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String parametroInvalido() { return "error/400"; }
}
