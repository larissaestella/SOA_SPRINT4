package com.careplus.map.model.vo;

import lombok.*;
import java.util.Map;

@Value
@Builder
public class EstatisticasVO {

    Long totalMissoesCompletadas;
    Map<String, Long> missoesPorCategoria;
    Integer pontosTotal;
    Integer nivelAtual;
    Integer pontosParaProximoNivel;
}
