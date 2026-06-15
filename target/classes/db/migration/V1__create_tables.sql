
-- Tabela de Usuários
CREATE TABLE usuarios (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome            VARCHAR(100)        NOT NULL,
    email           VARCHAR(150)        NOT NULL UNIQUE,
    data_nascimento DATE                NOT NULL,
    criado_em       TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em   TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Avatares (1:1 com usuário)
CREATE TABLE avatares (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id      BIGINT             NOT NULL UNIQUE,
    nome_avatar     VARCHAR(80)        NOT NULL,
    nivel           INT                NOT NULL DEFAULT 1,
    pontos_total    INT                NOT NULL DEFAULT 0,
    saude           INT                NOT NULL DEFAULT 0,
    hidratacao      INT                NOT NULL DEFAULT 0,
    sono            INT                NOT NULL DEFAULT 0,
    exercicio       INT                NOT NULL DEFAULT 0,
    bem_estar       INT                NOT NULL DEFAULT 0,
    criado_em       TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em   TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_avatar_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabela de Missões disponíveis
CREATE TABLE missoes (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo            VARCHAR(120)       NOT NULL,
    descricao         VARCHAR(500)       NOT NULL,
    categoria         VARCHAR(50)        NOT NULL,   -- SAUDE, HIDRATACAO, SONO, EXERCICIO, BEM_ESTAR
    pontos_recompensa INT                NOT NULL DEFAULT 10,
    bonus_saude       INT                NOT NULL DEFAULT 0,
    bonus_hidratacao  INT                NOT NULL DEFAULT 0,
    bonus_sono        INT                NOT NULL DEFAULT 0,
    bonus_exercicio   INT                NOT NULL DEFAULT 0,
    bonus_bem_estar   INT                NOT NULL DEFAULT 0,
    ativa             BOOLEAN            NOT NULL DEFAULT TRUE,
    criado_em         TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Missões Completadas pelo usuário
CREATE TABLE missoes_completadas (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id      BIGINT             NOT NULL,
    missao_id       BIGINT             NOT NULL,
    completada_em   TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacao      VARCHAR(300),
    CONSTRAINT fk_mc_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_mc_missao  FOREIGN KEY (missao_id)  REFERENCES missoes(id)
);

-- Índices para performance
CREATE INDEX idx_mc_usuario ON missoes_completadas(usuario_id);
CREATE INDEX idx_mc_missao  ON missoes_completadas(missao_id);
CREATE INDEX idx_mc_data    ON missoes_completadas(completada_em);