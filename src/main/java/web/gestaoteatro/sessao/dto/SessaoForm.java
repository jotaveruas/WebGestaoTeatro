package web.gestaoteatro.sessao.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

public class SessaoForm {

    @NotNull(message = "Selecione uma peça.")
    @Positive(message = "Selecione uma peça válida.")
    private Long codigoPeca;

    @NotNull(message = "Informe a data.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate data;

    @NotNull(message = "Informe o horário.")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horario;

    @NotBlank(message = "Informe o local.")
    @Size(max = 160, message = "O local deve ter até 160 caracteres.")
    private String local;

    @NotNull(message = "Informe a capacidade.")
    @Positive(message = "A capacidade deve ser maior que zero.")
    private Integer capacidade;

    @NotNull(message = "Informe o valor.")
    @DecimalMin(value = "0.00", message = "O valor não pode ser negativo.")
    @Digits(integer = 8, fraction = 2, message = "Use até 8 dígitos inteiros e 2 casas decimais.")
    private BigDecimal valor;

    private Long versao;

    public SessaoForm() {
    }

    public Long getCodigoPeca() {
        return codigoPeca;
    }

    public void setCodigoPeca(Long codigoPeca) {
        this.codigoPeca = codigoPeca;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }
}
