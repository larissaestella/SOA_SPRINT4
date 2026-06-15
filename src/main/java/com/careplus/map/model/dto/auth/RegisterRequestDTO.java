package com.careplus.map.model.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Dados para registro de novo usuário")
public class RegisterRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Schema(example = "João Silva")
    private String nome;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Schema(example = "joao@email.com")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    @Schema(example = "Senha@123")
    private String senha;

    @NotNull(message = "Data de nascimento é obrigatória")
    @Schema(example = "1990-05-15")
    private LocalDate dataNascimento;

    @NotBlank(message = "Nome do avatar é obrigatório")
    @Size(max = 80, message = "Nome do avatar deve ter no máximo 80 caracteres")
    @Schema(example = "MeuAvatarSaudavel")
    private String nomeAvatar;
}
