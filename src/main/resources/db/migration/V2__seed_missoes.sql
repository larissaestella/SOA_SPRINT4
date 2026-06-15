
INSERT INTO missoes (titulo, descricao, categoria, pontos_recompensa, bonus_saude, bonus_hidratacao, bonus_sono, bonus_exercicio, bonus_bem_estar) VALUES
-- Missões de SAÚDE
('Check-up Anual Agendado',   'Agende seu check-up anual com seu médico de referência.',          'SAUDE',     30, 30, 0,  0, 0, 0),
('Vacina em Dia',             'Registre que tomou a vacina prevista no calendário vacinal.',        'SAUDE',     25, 25, 0,  0, 0, 0),
('Consulta Odontológica',     'Realize sua consulta odontológica semestral.',                       'SAUDE',     20, 20, 0,  0, 0, 0),

-- Missões de HIDRATAÇÃO
('2L de Água Hoje',           'Registre que bebeu pelo menos 2 litros de água durante o dia.',     'HIDRATACAO', 10, 0, 20,  0, 0, 0),
('Chá Sem Açúcar',            'Substitua uma bebida açucarada por chá natural sem açúcar.',        'HIDRATACAO',  5, 0, 10,  0, 0, 0),

-- Missões de SONO
('8h de Sono',                'Durma pelo menos 8 horas e registre no app.',                       'SONO',       15, 0, 0, 25, 0, 0),
('Rotina Noturna',            'Estabeleça e siga uma rotina noturna sem telas por 30 minutos.',    'SONO',       10, 0, 0, 15, 0, 5),

-- Missões de EXERCÍCIO
('30 min de Caminhada',       'Caminhe por pelo menos 30 minutos hoje.',                           'EXERCICIO',  15, 0, 0,  0, 25, 0),
('Alongamento Matinal',       'Realize 10 minutos de alongamento ao acordar.',                     'EXERCICIO',   8, 0, 0,  0, 15, 5),
('Escada no Lugar do Elevador','Use a escada em vez do elevador pelo menos uma vez hoje.',          'EXERCICIO',   5, 0, 0,  0, 10, 0),

-- Missões de BEM-ESTAR
('Meditação Guiada',          'Complete uma sessão de meditação guiada de pelo menos 10 minutos.', 'BEM_ESTAR',  12, 0, 0,  0, 0, 25),
('Jogo Cognitivo',            'Jogue um jogo cognitivo por 15 minutos para estimular o cérebro.',  'BEM_ESTAR',  10, 0, 0,  0, 0, 20),
('Momento de Leitura',        'Leia por pelo menos 20 minutos sem interrupções.',                  'BEM_ESTAR',   8, 0, 0,  5, 0, 15),
('Gratidão Diária',           'Escreva 3 coisas pelas quais você é grato hoje.',                   'BEM_ESTAR',   6, 0, 0,  0, 0, 10);