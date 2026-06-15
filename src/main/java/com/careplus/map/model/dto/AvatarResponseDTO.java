package com.careplus.map.model.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvatarResponseDTO {
    private Long id;
    private Long usuarioId;
    private String nomeAvatar;
    private Integer nivel;
    private Integer pontosTotal;
    private Integer saude;
    private Integer hidratacao;
    private Integer sono;
    private Integer exercicio;
    private Integer bemEstar;
    private LocalDateTime atualizadoEm;
}