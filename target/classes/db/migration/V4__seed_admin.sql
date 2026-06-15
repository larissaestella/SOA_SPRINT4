-- Sprint 4: Insere usuário administrador padrão da plataforma MAP
-- Credenciais: admin@careplus.com.br / Admin@CareP1us
-- Hash BCrypt (strength 10) gerado para a senha acima
INSERT INTO usuarios (nome, email, senha, data_nascimento, perfil, criado_em, atualizado_em)
VALUES (
    'Administrador MAP',
    'admin@careplus.com.br',
    '$2a$10$uXZnO5NteGRn3O32baPTXu/VaSlD0hLbgQ70IZ6xCF4gX9TBXS3hW',
    '1990-01-01',
    'ADMIN',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Avatar do admin (necessário pelo relacionamento 1:1 mapeado na entidade)
INSERT INTO avatares (usuario_id, nome_avatar, nivel, pontos_total, saude, hidratacao, sono, exercicio, bem_estar, criado_em, atualizado_em)
VALUES (
    (SELECT id FROM usuarios WHERE email = 'admin@careplus.com.br'),
    'AdminAvatar',
    1, 0, 0, 0, 0, 0, 0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
