package web.gestaoteatro.inscricao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web.gestaoteatro.inscricao.Inscricao;

public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {
    boolean existsBySessaoCodigo(Long codigo);
}
