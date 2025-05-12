-- Tabela de usuários
CREATE TABLE usuario (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  senha VARCHAR(255) NOT NULL
);

-- Tabela de categorias
CREATE TABLE categoria (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(100) NOT NULL
);

-- Tabela de listas
CREATE TABLE lista (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  titulo VARCHAR(150) NOT NULL,
  descricao TEXT,
  categoria_id BIGINT,
  usuario_id BIGINT,
  FOREIGN KEY (categoria_id) REFERENCES categoria(id),
  FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);
