package com.careplus.map.model.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Credenciais para autenticação")
public class LoginRequestDTO {

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Schema(description = "E-mail do usuário", example = "usuario@email.com")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Schema(description = "Senha do usuário", example = "Senha@123")
    private String senha;
}
