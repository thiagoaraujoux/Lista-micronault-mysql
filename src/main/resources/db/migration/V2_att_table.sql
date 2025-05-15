-- V2 do Schema do Banco de Dados

-- Mantém a tabela de usuários (inalterada do V1)
CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL
);

-- Mantém a tabela de categorias (inalterada do V1)
CREATE TABLE IF NOT EXISTS categoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

-- Tabela de listas (V2)
-- Adiciona a coluna 'status'
CREATE TABLE IF NOT EXISTS lista (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT,
    categoria_id BIGINT,
    usuario_id BIGINT,
    -- Nova coluna para o status da lista
    -- Armazena o nome do enum (ATIVA, CONCLUIDA, ARQUIVADA)
    status VARCHAR(50) NOT NULL DEFAULT 'ATIVA', -- Define como NOT NULL e com valor padrão 'ATIVA'
    FOREIGN KEY (categoria_id) REFERENCES categoria(id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- Se você já tem dados na tabela 'lista' do V1,
-- e quer adicionar a coluna 'status' sem perder os dados,
-- você usaria um comando ALTER TABLE em vez de CREATE TABLE IF NOT EXISTS lista.
-- Exemplo de ALTER TABLE para adicionar a coluna 'status' a uma tabela existente:
/*
ALTER TABLE lista
ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'ATIVA';
*/

-- Nota: Se você estiver usando um sistema de migração de banco de dados
-- (como Flyway ou Liquibase), você criaria um script de migração
-- que contenha apenas o comando ALTER TABLE para adicionar a nova coluna.

