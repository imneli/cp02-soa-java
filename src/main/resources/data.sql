INSERT INTO salas (nome, capacidade, localizacao) VALUES ('Sala Inovação', 20, 'Bloco A - 2º andar');
INSERT INTO salas (nome, capacidade, localizacao) VALUES ('Sala Executiva', 8, 'Bloco B - 1º andar');
INSERT INTO salas (nome, capacidade, localizacao) VALUES ('Auditório', 50, 'Bloco C - Térreo');

INSERT INTO reservas (sala_id, data_hora_inicio, data_hora_fim, responsavel)
    VALUES (1, '2026-05-02 09:00:00', '2026-05-02 10:00:00', 'João Silva');
INSERT INTO reservas (sala_id, data_hora_inicio, data_hora_fim, responsavel)
    VALUES (2, '2026-05-02 14:00:00', '2026-05-02 15:30:00', 'Maria Santos');
