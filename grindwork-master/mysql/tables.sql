CREATE TABLE IF NOT EXISTS usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    perfil INT DEFAULT 0 COMMENT '0 = usuario; 1 = admin;',
    telefone VARCHAR(20),
    localidade_id INT,
    endereco TEXT,
    data_nascimento DATE,
    imagem_url varchar(255) DEFAULT NULL,
    status INT DEFAULT 1 COMMENT '1 = ativo; 2 = bloqueado;'
);
CREATE TABLE IF NOT EXISTS localidade (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cidade VARCHAR(255) NOT NULL,
    estado VARCHAR(255) NOT NULL
);
CREATE TABLE IF NOT EXISTS anuncio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    preco DECIMAL(10, 2),
    nota DECIMAL(10, 2) DEFAULT 0.0,
    usuario_id INT,
    localidade_id INT,
    endereco TEXT,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status INT DEFAULT 1 COMMENT '1 = ativo; 0 = desativado',
    imagem_url varchar(255) DEFAULT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (localidade_id) REFERENCES localidade(id)
);
CREATE TABLE IF NOT EXISTS avaliacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    anuncio_id INT,
    usuario_id INT,
    nota decimal(10,1),
    comentario TEXT,
    data_avaliacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (anuncio_id) REFERENCES anuncio(id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);
    