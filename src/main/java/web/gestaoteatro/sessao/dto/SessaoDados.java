package web.gestaoteatro.sessao.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import web.gestaoteatro.sessao.Sessao;

public record SessaoDados(Long codigo, Long versao, Long codigoPeca, String nomePeca,
        LocalDate data, LocalTime horario, String local, Integer capacidade, BigDecimal valor) {
    public static SessaoDados de(Sessao sessao) {
        return new SessaoDados(sessao.getCodigo(), sessao.getVersao(), sessao.getPeca().getCodigo(),
                sessao.getPeca().getNome(), sessao.getData(), sessao.getHorario(),
                sessao.getLocal(), sessao.getCapacidade(), sessao.getValor());
    }
}
