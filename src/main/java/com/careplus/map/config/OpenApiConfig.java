package com.careplus.map.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.*;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Swagger/OpenAPI com suporte a autenticação JWT (Bearer).
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MAP API - Meu Avatar Preventivo",
                version = "1.0",
                description = "API REST para o módulo de gamificação preventiva da Care Plus. " +
                              "Permite gerenciar usuários, avatares e missões de saúde preventiva. " +
                              "Use /api/v1/auth/registrar para criar uma conta e /api/v1/auth/login para obter o token JWT.",
                contact = @Contact(name = "Time MAP", email = "map@careplus.com.br")
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Ambiente local de desenvolvimento")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Token JWT obtido em POST /api/v1/auth/login. Formato: Bearer <token>"
)
public class OpenApiConfig {
}
