package com.careplus.map.model.vo;

import lombok.*;

@Value
@Builder
public class RankingVO {
    Integer posicao;
    Long usuarioId;
    String nomeUsuario;
    String nomeAvatar;
    Integer nivel;
    Integer pontosTotal;
    Long totalMissoesCompletadas;
}
