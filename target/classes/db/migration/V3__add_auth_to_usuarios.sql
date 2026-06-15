-- Sprint 4: Adiciona campos de autenticação à tabela de usuários
ALTER TABLE usuarios
ADD COLUMN IF NOT EXISTS senha VARCHAR(255) NOT NULL DEFAULT '$2a$10$placeholder';

ALTER TABLE usuarios
ADD COLUMN IF NOT EXISTS perfil VARCHAR(20) NOT NULL DEFAULT 'USUARIO';
