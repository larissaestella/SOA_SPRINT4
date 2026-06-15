package com.careplus.map.repository;

import com.careplus.map.model.entity.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {

    Optional<Avatar> findByUsuarioId(Long usuarioId);

    @Query("SELECT a FROM Avatar a JOIN FETCH a.usuario ORDER BY a.pontosTotal DESC")
    List<Avatar> findRankingGlobal();
}
