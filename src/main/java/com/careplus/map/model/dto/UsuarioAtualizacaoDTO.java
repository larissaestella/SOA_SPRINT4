package com.careplus.map.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioAtualizacaoDTO {

    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @Email(message = "Formato de e-mail inválido")
    private String email;

    @Past(message = "A data de nascimento deve ser no passado")
    private LocalDate dataNascimento;
}
