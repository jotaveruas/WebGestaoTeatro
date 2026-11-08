package web.gestaoteatro.sessao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import web.gestaoteatro.sessao.Sessao;

public interface SessaoRepository extends JpaRepository<Sessao, Long>, JpaSpecificationExecutor<Sessao> {
    @Override
    @EntityGraph(attributePaths = "peca")
    Page<Sessao> findAll(Specification<Sessao> filtro, Pageable pageable);
}
