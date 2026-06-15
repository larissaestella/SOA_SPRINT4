package com.careplus.map.repository;

import com.careplus.map.model.entity.Missao;
import com.careplus.map.model.enums.CategoriaMissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MissaoRepository extends JpaRepository<Missao, Long> {

    List<Missao> findByAtivaTrue();

    List<Missao> findByAtivaAndCategoria(Boolean ativa, CategoriaMissao categoria);
}
