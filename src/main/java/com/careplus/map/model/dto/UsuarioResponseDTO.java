package com.careplus.map.model.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private LocalDate dataNascimento;
    private LocalDateTime criadoEm;
}
