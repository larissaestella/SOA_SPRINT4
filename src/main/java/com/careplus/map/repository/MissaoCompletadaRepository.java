package com.careplus.map.repository;

import com.careplus.map.model.entity.MissaoCompletada;
import com.careplus.map.model.enums.CategoriaMissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MissaoCompletadaRepository extends JpaRepository<MissaoCompletada, Long> {

    long countByUsuarioId(Long usuarioId);

    List<MissaoCompletada> findByUsuarioIdOrderByCompletadaEmDesc(Long usuarioId);

    @Query("""
        SELECT mc.missao.categoria, COUNT(mc)
        FROM MissaoCompletada mc
        WHERE mc.usuario.id = :usuarioId
        GROUP BY mc.missao.categoria
        """)
    List<Object[]> countMissoesPorCategoria(@Param("usuarioId") Long usuarioId);
}
