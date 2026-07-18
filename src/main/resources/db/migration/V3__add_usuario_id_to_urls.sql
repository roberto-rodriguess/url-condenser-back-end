ALTER TABLE tb_urls ADD COLUMN usuario_id BIGINT;
ALTER TABLE tb_urls ADD CONSTRAINT fk_tb_urls_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE;
