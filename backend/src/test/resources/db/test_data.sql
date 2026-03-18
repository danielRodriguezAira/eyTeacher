-- ============================================================
-- TRUNCATE ALL TABLES (respecting FK constraints)
-- ============================================================
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE solutions;
TRUNCATE TABLE tasks;
TRUNCATE TABLE topics_students;
TRUNCATE TABLE topics;
TRUNCATE TABLE categories;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- TEST DATA SCRIPT
-- 20 users (5 teachers/owners + 15 students)
-- 1-3 categories per teacher (only 5 teachers)
-- 2-3 topics per category
-- 2-3 tasks per topic
-- At least 2 students per topic
-- 1-2 solutions per task (from enrolled students)
-- ============================================================

-- USERS (UUIDs stored as BINARY(16))
-- 5 teachers: user_01 to user_05
-- 15 students: user_06 to user_20
INSERT INTO users (id, email, password, first_name, last_name, is_admin) VALUES
(UNHEX(REPLACE('00000000-0000-0000-0000-000000000001', '-', '')), 'teacher1@test.com', '$2a$10$xhmwkQ.0yo1hapMABoLubuNYhPL81L8YyKr5/Yb3PqRx70nSpe/Vy', 'Ana',     'García',    FALSE),
(UNHEX(REPLACE('00000000-0000-0000-0000-000000000002', '-', '')), 'teacher2@test.com', '$2a$10$xhmwkQ.0yo1hapMABoLubuNYhPL81L8YyKr5/Yb3PqRx70nSpe/Vy', 'Carlos',  'López',     FALSE),
(UNHEX(REPLACE('00000000-0000-0000-0000-000000000003', '-', '')), 'teacher3@test.com', '$2a$10$xhmwkQ.0yo1hapMABoLubuNYhPL81L8YyKr5/Yb3PqRx70nSpe/Vy', 'María',   'Martínez',  FALSE),
(UNHEX(REPLACE('00000000-0000-0000-0000-000000000004', '-', '')), 'teacher4@test.com', '$2a$10$xhmwkQ.0yo1hapMABoLubuNYhPL81L8YyKr5/Yb3PqRx70nSpe/Vy', 'Pedro',   'Sánchez',   FALSE),
(UNHEX(REPLACE('00000000-0000-0000-0000-000000000005', '-', '')), 'teacher5@test.com', '$2a$10$xhmwkQ.0yo1hapMABoLubuNYhPL81L8YyKr5/Yb3PqRx70nSpe/Vy', 'Laura',   'Fernández', FALSE),
(UNHEX(REPLACE('00000000-0000-0000-0000-000000000006', '-', '')), 'student1@test.com', '$2a$10$xhmwkQ.0yo1hapMABoLubuNYhPL81L8YyKr5/Yb3PqRx70nSpe/Vy', 'Sofía',   'Ruiz',      FALSE),
(UNHEX(REPLACE('00000000-0000-0000-0000-000000000007', '-', '')), 'student2@test.com', '$2a$10$xhmwkQ.0yo1hapMABoLubuNYhPL81L8YyKr5/Yb3PqRx70nSpe/Vy', 'Miguel',  'Torres',    FALSE),
(UNHEX(REPLACE('00000000-0000-0000-0000-000000000008', '-', '')), 'student3@test.com', '$2a$10$xhmwkQ.0yo1hapMABoLubuNYhPL81L8YyKr5/Yb3PqRx70nSpe/Vy', 'Elena',   'Díaz',      FALSE),
(UNHEX(REPLACE('00000000-0000-0000-0000-000000000009', '-', '')), 'student4@test.com', '$2a$10$xhmwkQ.0yo1hapMABoLubuNYhPL81L8YyKr5/Yb3PqRx70nSpe/Vy', 'Javier',  'Moreno',    FALSE),
(UNHEX(REPLACE('00000000-0000-0000-0000-000000000010', '-', '')), 'student5@test.com', '$2a$10$xhmwkQ.0yo1hapMABoLubuNYhPL81L8YyKr5/Yb3PqRx70nSpe/Vy', 'Lucía',   'Jiménez',   FALSE),
(UNHEX(REPLACE('00000000-0000-0000-0000-000000000011', '-', '')), 'student6@test.com', '$2a$10$xhmwkQ.0yo1hapMABoLubuNYhPL81L8YyKr5/Yb3PqRx70nSpe/Vy', 'Pablo',   'Álvarez',   FALSE),
(UNHEX(REPLACE('00000000-0000-0000-0000-000000000012', '-', '')), 'student7@test.com', '$2a$10$xhmwkQ.0yo1hapMABoLubuNYhPL81L8YyKr5/Yb3PqRx70nSpe/Vy', 'Carmen',  'Romero',    FALSE),
(UNHEX(REPLACE('00000000-0000-0000-0000-000000000013', '-', '')), 'student8@test.com', '$2a$10$xhmwkQ.0yo1hapMABoLubuNYhPL81L8YyKr5/Yb3PqRx70nSpe/Vy', 'Andrés',  'Navarro',   FALSE),
(UNHEX(REPLACE('00000000-0000-0000-0000-000000000014', '-', '')), 'student9@test.com', '$2a$10$xhmwkQ.0yo1hapMABoLubuNYhPL81L8YyKr5/Yb3PqRx70nSpe/Vy', 'Isabel',  'Gutiérrez', FALSE),
(UNHEX(REPLACE('00000000-0000-0000-0000-000000000015', '-', '')), 'student10@test.com','$2a$10$xhmwkQ.0yo1hapMABoLubuNYhPL81L8YyKr5/Yb3PqRx70nSpe/Vy', 'Diego',   'Serrano',   FALSE),
(UNHEX(REPLACE('00000000-0000-0000-0000-000000000016', '-', '')), 'student11@test.com','$2a$10$xhmwkQ.0yo1hapMABoLubuNYhPL81L8YyKr5/Yb3PqRx70nSpe/Vy', 'Marta',   'Blanco',    FALSE),
(UNHEX(REPLACE('00000000-0000-0000-0000-000000000017', '-', '')), 'student12@test.com','$2a$10$xhmwkQ.0yo1hapMABoLubuNYhPL81L8YyKr5/Yb3PqRx70nSpe/Vy', 'Raúl',    'Castro',    FALSE),
(UNHEX(REPLACE('00000000-0000-0000-0000-000000000018', '-', '')), 'student13@test.com','$2a$10$xhmwkQ.0yo1hapMABoLubuNYhPL81L8YyKr5/Yb3PqRx70nSpe/Vy', 'Nuria',   'Ortega',    FALSE),
(UNHEX(REPLACE('00000000-0000-0000-0000-000000000019', '-', '')), 'student14@test.com','$2a$10$xhmwkQ.0yo1hapMABoLubuNYhPL81L8YyKr5/Yb3PqRx70nSpe/Vy', 'Víctor',  'Molina',    FALSE),
(UNHEX(REPLACE('00000000-0000-0000-0000-000000000020', '-', '')), 'student15@test.com','$2a$10$xhmwkQ.0yo1hapMABoLubuNYhPL81L8YyKr5/Yb3PqRx70nSpe/Vy', 'Alicia',  'Delgado',   FALSE);

-- ============================================================
-- CATEGORIES
-- teacher1 (user_01): 3 categories
-- teacher2 (user_02): 2 categories
-- teacher3 (user_03): 1 category
-- teacher4 (user_04): 3 categories
-- teacher5 (user_05): 2 categories
-- Total: 11 categories
-- ============================================================
INSERT INTO categories (id, name, description, owner_id) VALUES
-- teacher1: 3 categories
(1,  'Matemáticas',        'Categoría de matemáticas generales',       UNHEX(REPLACE('00000000-0000-0000-0000-000000000001', '-', ''))),
(2,  'Física',             'Categoría de física básica',               UNHEX(REPLACE('00000000-0000-0000-0000-000000000001', '-', ''))),
(3,  'Química',            'Categoría de química general',             UNHEX(REPLACE('00000000-0000-0000-0000-000000000001', '-', ''))),
-- teacher2: 2 categories
(4,  'Historia',           'Categoría de historia universal',          UNHEX(REPLACE('00000000-0000-0000-0000-000000000002', '-', ''))),
(5,  'Geografía',          'Categoría de geografía mundial',           UNHEX(REPLACE('00000000-0000-0000-0000-000000000002', '-', ''))),
-- teacher3: 1 category
(6,  'Lengua',             'Categoría de lengua y literatura',         UNHEX(REPLACE('00000000-0000-0000-0000-000000000003', '-', ''))),
-- teacher4: 3 categories
(7,  'Inglés',             'Categoría de inglés',                      UNHEX(REPLACE('00000000-0000-0000-0000-000000000004', '-', ''))),
(8,  'Francés',            'Categoría de francés',                     UNHEX(REPLACE('00000000-0000-0000-0000-000000000004', '-', ''))),
(9,  'Alemán',             'Categoría de alemán',                      UNHEX(REPLACE('00000000-0000-0000-0000-000000000004', '-', ''))),
-- teacher5: 2 categories
(10, 'Programación',       'Categoría de programación',                UNHEX(REPLACE('00000000-0000-0000-0000-000000000005', '-', ''))),
(11, 'Bases de Datos',     'Categoría de bases de datos',              UNHEX(REPLACE('00000000-0000-0000-0000-000000000005', '-', '')));

-- ============================================================
-- TOPICS (2-3 per category, total: 27 topics)
-- ============================================================
INSERT INTO topics (id, name, description, category_id) VALUES
-- Category 1: Matemáticas (3 topics)
(1,  'Álgebra',            'Fundamentos de álgebra',                   1),
(2,  'Geometría',          'Geometría euclidiana',                     1),
(3,  'Cálculo',            'Introducción al cálculo',                  1),
-- Category 2: Física (2 topics)
(4,  'Mecánica',           'Mecánica clásica',                         2),
(5,  'Termodinámica',      'Principios de termodinámica',              2),
-- Category 3: Química (3 topics)
(6,  'Química Orgánica',   'Compuestos orgánicos',                     3),
(7,  'Química Inorgánica', 'Compuestos inorgánicos',                   3),
(8,  'Estequiometría',     'Cálculos estequiométricos',                3),
-- Category 4: Historia (2 topics)
(9,  'Historia Antigua',   'Civilizaciones antiguas',                  4),
(10, 'Historia Moderna',   'Edad moderna y contemporánea',             4),
-- Category 5: Geografía (3 topics)
(11, 'Geografía Física',   'Relieve y clima',                          5),
(12, 'Geografía Humana',   'Población y economía',                     5),
(13, 'Cartografía',        'Mapas y proyecciones',                     5),
-- Category 6: Lengua (2 topics)
(14, 'Gramática',          'Morfología y sintaxis',                    6),
(15, 'Literatura',         'Obras y autores literarios',               6),
-- Category 7: Inglés (3 topics)
(16, 'Grammar',            'English grammar fundamentals',             7),
(17, 'Vocabulary',         'English vocabulary building',              7),
(18, 'Writing',            'English writing skills',                   7),
-- Category 8: Francés (2 topics)
(19, 'Grammaire',          'Grammaire française',                      8),
(20, 'Conjugaison',        'Conjugaison des verbes',                   8),
-- Category 9: Alemán (3 topics)
(21, 'Grammatik',          'Deutsche Grammatik',                       9),
(22, 'Wortschatz',         'Deutscher Wortschatz',                     9),
(23, 'Aussprache',         'Deutsche Aussprache',                      9),
-- Category 10: Programación (2 topics)
(24, 'Java',               'Programación en Java',                     10),
(25, 'Python',             'Programación en Python',                   10),
-- Category 11: Bases de Datos (2 topics)
(26, 'SQL',                'Lenguaje SQL',                             11),
(27, 'NoSQL',              'Bases de datos NoSQL',                     11);

-- ============================================================
-- TASKS (2-3 per topic, total: 65 tasks)
-- ============================================================
INSERT INTO tasks (id, description, topic_id) VALUES
-- Topic 1: Álgebra (3 tasks)
(1,  'Resolver sistemas de ecuaciones lineales',                        1),
(2,  'Factorizar polinomios de segundo grado',                          1),
(3,  'Simplificar expresiones algebraicas con fracciones',              1),
-- Topic 2: Geometría (2 tasks)
(4,  'Calcular el área y perímetro de figuras planas',                  2),
(5,  'Demostrar el teorema de Pitágoras',                               2),
-- Topic 3: Cálculo (3 tasks)
(6,  'Calcular límites de funciones',                                   3),
(7,  'Derivar funciones compuestas usando la regla de la cadena',       3),
(8,  'Calcular integrales definidas',                                   3),
-- Topic 4: Mecánica (2 tasks)
(9,  'Aplicar las leyes de Newton a problemas de movimiento',           4),
(10, 'Calcular trabajo y energía cinética',                             4),
-- Topic 5: Termodinámica (3 tasks)
(11, 'Aplicar el primer principio de la termodinámica',                 5),
(12, 'Calcular eficiencia de ciclos termodinámicos',                    5),
(13, 'Analizar procesos isotérmicos e isobáricos',                      5),
-- Topic 6: Química Orgánica (2 tasks)
(14, 'Identificar grupos funcionales en moléculas orgánicas',           6),
(15, 'Nombrar compuestos orgánicos según la IUPAC',                     6),
-- Topic 7: Química Inorgánica (3 tasks)
(16, 'Clasificar óxidos, hidróxidos, ácidos y sales',                   7),
(17, 'Balancear reacciones de oxidación-reducción',                     7),
(18, 'Calcular el pH de soluciones ácidas y básicas',                   7),
-- Topic 8: Estequiometría (2 tasks)
(19, 'Calcular masas molares y número de moles',                        8),
(20, 'Resolver problemas de reactivo limitante',                        8),
-- Topic 9: Historia Antigua (3 tasks)
(21, 'Describir las principales civilizaciones mesopotámicas',          9),
(22, 'Analizar la organización política de la Antigua Grecia',          9),
(23, 'Comparar el Imperio Romano con el Imperio Persa',                 9),
-- Topic 10: Historia Moderna (2 tasks)
(24, 'Explicar las causas de la Revolución Francesa',                   10),
(25, 'Analizar el impacto de la Revolución Industrial',                 10),
-- Topic 11: Geografía Física (3 tasks)
(26, 'Identificar los principales tipos de relieve terrestre',          11),
(27, 'Clasificar los climas del mundo según Köppen',                    11),
(28, 'Analizar los factores que influyen en el clima',                  11),
-- Topic 12: Geografía Humana (2 tasks)
(29, 'Analizar la distribución de la población mundial',                12),
(30, 'Comparar modelos económicos de países desarrollados y en vías de desarrollo', 12),
-- Topic 13: Cartografía (2 tasks)
(31, 'Interpretar coordenadas geográficas en un mapa',                  13),
(32, 'Comparar diferentes proyecciones cartográficas',                  13),
-- Topic 14: Gramática (3 tasks)
(33, 'Analizar la estructura morfológica de palabras',                  14),
(34, 'Identificar las funciones sintácticas en oraciones complejas',    14),
(35, 'Clasificar los tipos de oraciones subordinadas',                  14),
-- Topic 15: Literatura (2 tasks)
(36, 'Analizar las características del Romanticismo literario',         15),
(37, 'Comentar un texto narrativo del siglo XX',                        15),
-- Topic 16: Grammar (3 tasks)
(38, 'Use of tenses: present perfect vs simple past',                   16),
(39, 'Conditional sentences: types 1, 2 and 3',                        16),
(40, 'Passive voice transformation exercises',                          16),
-- Topic 17: Vocabulary (2 tasks)
(41, 'Learn and use collocations in context',                           17),
(42, 'Identify and use phrasal verbs in sentences',                     17),
-- Topic 18: Writing (3 tasks)
(43, 'Write a formal letter of complaint',                              18),
(44, 'Write an argumentative essay on a current topic',                 18),
(45, 'Write a report summarizing data from a chart',                    18),
-- Topic 19: Grammaire (2 tasks)
(46, 'Accorder les adjectifs en genre et en nombre',                    19),
(47, 'Utiliser les pronoms relatifs correctement',                      19),
-- Topic 20: Conjugaison (3 tasks)
(48, 'Conjuguer les verbes au subjonctif présent',                      20),
(49, 'Utiliser le conditionnel passé dans des phrases',                 20),
(50, 'Conjuguer les verbes irréguliers au passé composé',               20),
-- Topic 21: Grammatik (2 tasks)
(51, 'Deklination der Adjektive im Deutschen',                          21),
(52, 'Verwendung der Modalverben im Deutschen',                         21),
-- Topic 22: Wortschatz (3 tasks)
(53, 'Lernen von zusammengesetzten Substantiven',                       22),
(54, 'Verwendung von Präpositionen mit Dativ und Akkusativ',            22),
(55, 'Synonyme und Antonyme im Deutschen',                              22),
-- Topic 23: Aussprache (2 tasks)
(56, 'Übungen zur deutschen Vokalaussprache',                           23),
(57, 'Intonation und Betonung in deutschen Sätzen',                     23),
-- Topic 24: Java (3 tasks)
(58, 'Implementar una clase con herencia y polimorfismo',               24),
(59, 'Crear una aplicación con colecciones y streams',                  24),
(60, 'Implementar patrones de diseño: Singleton y Factory',             24),
-- Topic 25: Python (2 tasks)
(61, 'Crear funciones con decoradores en Python',                       25),
(62, 'Implementar una API REST con Flask',                              25),
-- Topic 26: SQL (3 tasks)
(63, 'Escribir consultas con JOINs múltiples',                         26),
(64, 'Crear procedimientos almacenados y triggers',                     26),
(65, 'Optimizar consultas con índices',                                 26),
-- Topic 27: NoSQL (2 tasks)
(66, 'Diseñar un esquema de documentos en MongoDB',                     27),
(67, 'Implementar operaciones CRUD en Redis',                           27);

-- ============================================================
-- TOPICS_STUDENTS
-- Each topic has at least 2 students enrolled
-- Students: user_06 to user_20 (IDs 6-20)
-- ============================================================
INSERT INTO topics_students (topic_id, student_id) VALUES
-- Topic 1: Álgebra (students 6,7,8)
(1,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000006', '-', ''))),
(1,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000007', '-', ''))),
(1,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000008', '-', ''))),
-- Topic 2: Geometría (students 6,9)
(2,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000006', '-', ''))),
(2,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000009', '-', ''))),
-- Topic 3: Cálculo (students 7,8,10)
(3,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000007', '-', ''))),
(3,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000008', '-', ''))),
(3,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000010', '-', ''))),
-- Topic 4: Mecánica (students 9,11)
(4,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000009', '-', ''))),
(4,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000011', '-', ''))),
-- Topic 5: Termodinámica (students 10,11,12)
(5,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000010', '-', ''))),
(5,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000011', '-', ''))),
(5,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000012', '-', ''))),
-- Topic 6: Química Orgánica (students 12,13)
(6,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000012', '-', ''))),
(6,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000013', '-', ''))),
-- Topic 7: Química Inorgánica (students 13,14,15)
(7,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000013', '-', ''))),
(7,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000014', '-', ''))),
(7,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000015', '-', ''))),
-- Topic 8: Estequiometría (students 14,16)
(8,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000014', '-', ''))),
(8,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000016', '-', ''))),
-- Topic 9: Historia Antigua (students 15,16,17)
(9,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000015', '-', ''))),
(9,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000016', '-', ''))),
(9,  UNHEX(REPLACE('00000000-0000-0000-0000-000000000017', '-', ''))),
-- Topic 10: Historia Moderna (students 17,18)
(10, UNHEX(REPLACE('00000000-0000-0000-0000-000000000017', '-', ''))),
(10, UNHEX(REPLACE('00000000-0000-0000-0000-000000000018', '-', ''))),
-- Topic 11: Geografía Física (students 18,19,20)
(11, UNHEX(REPLACE('00000000-0000-0000-0000-000000000018', '-', ''))),
(11, UNHEX(REPLACE('00000000-0000-0000-0000-000000000019', '-', ''))),
(11, UNHEX(REPLACE('00000000-0000-0000-0000-000000000020', '-', ''))),
-- Topic 12: Geografía Humana (students 6,19)
(12, UNHEX(REPLACE('00000000-0000-0000-0000-000000000006', '-', ''))),
(12, UNHEX(REPLACE('00000000-0000-0000-0000-000000000019', '-', ''))),
-- Topic 13: Cartografía (students 7,20)
(13, UNHEX(REPLACE('00000000-0000-0000-0000-000000000007', '-', ''))),
(13, UNHEX(REPLACE('00000000-0000-0000-0000-000000000020', '-', ''))),
-- Topic 14: Gramática (students 8,9,10)
(14, UNHEX(REPLACE('00000000-0000-0000-0000-000000000008', '-', ''))),
(14, UNHEX(REPLACE('00000000-0000-0000-0000-000000000009', '-', ''))),
(14, UNHEX(REPLACE('00000000-0000-0000-0000-000000000010', '-', ''))),
-- Topic 15: Literatura (students 11,12)
(15, UNHEX(REPLACE('00000000-0000-0000-0000-000000000011', '-', ''))),
(15, UNHEX(REPLACE('00000000-0000-0000-0000-000000000012', '-', ''))),
-- Topic 16: Grammar (students 13,14,15)
(16, UNHEX(REPLACE('00000000-0000-0000-0000-000000000013', '-', ''))),
(16, UNHEX(REPLACE('00000000-0000-0000-0000-000000000014', '-', ''))),
(16, UNHEX(REPLACE('00000000-0000-0000-0000-000000000015', '-', ''))),
-- Topic 17: Vocabulary (students 16,17)
(17, UNHEX(REPLACE('00000000-0000-0000-0000-000000000016', '-', ''))),
(17, UNHEX(REPLACE('00000000-0000-0000-0000-000000000017', '-', ''))),
-- Topic 18: Writing (students 18,19,20)
(18, UNHEX(REPLACE('00000000-0000-0000-0000-000000000018', '-', ''))),
(18, UNHEX(REPLACE('00000000-0000-0000-0000-000000000019', '-', ''))),
(18, UNHEX(REPLACE('00000000-0000-0000-0000-000000000020', '-', ''))),
-- Topic 19: Grammaire (students 6,8)
(19, UNHEX(REPLACE('00000000-0000-0000-0000-000000000006', '-', ''))),
(19, UNHEX(REPLACE('00000000-0000-0000-0000-000000000008', '-', ''))),
-- Topic 20: Conjugaison (students 7,9,11)
(20, UNHEX(REPLACE('00000000-0000-0000-0000-000000000007', '-', ''))),
(20, UNHEX(REPLACE('00000000-0000-0000-0000-000000000009', '-', ''))),
(20, UNHEX(REPLACE('00000000-0000-0000-0000-000000000011', '-', ''))),
-- Topic 21: Grammatik (students 10,12)
(21, UNHEX(REPLACE('00000000-0000-0000-0000-000000000010', '-', ''))),
(21, UNHEX(REPLACE('00000000-0000-0000-0000-000000000012', '-', ''))),
-- Topic 22: Wortschatz (students 13,15,17)
(22, UNHEX(REPLACE('00000000-0000-0000-0000-000000000013', '-', ''))),
(22, UNHEX(REPLACE('00000000-0000-0000-0000-000000000015', '-', ''))),
(22, UNHEX(REPLACE('00000000-0000-0000-0000-000000000017', '-', ''))),
-- Topic 23: Aussprache (students 14,16)
(23, UNHEX(REPLACE('00000000-0000-0000-0000-000000000014', '-', ''))),
(23, UNHEX(REPLACE('00000000-0000-0000-0000-000000000016', '-', ''))),
-- Topic 24: Java (students 18,19,20)
(24, UNHEX(REPLACE('00000000-0000-0000-0000-000000000018', '-', ''))),
(24, UNHEX(REPLACE('00000000-0000-0000-0000-000000000019', '-', ''))),
(24, UNHEX(REPLACE('00000000-0000-0000-0000-000000000020', '-', ''))),
-- Topic 25: Python (students 6,7)
(25, UNHEX(REPLACE('00000000-0000-0000-0000-000000000006', '-', ''))),
(25, UNHEX(REPLACE('00000000-0000-0000-0000-000000000007', '-', ''))),
-- Topic 26: SQL (students 8,9,10)
(26, UNHEX(REPLACE('00000000-0000-0000-0000-000000000008', '-', ''))),
(26, UNHEX(REPLACE('00000000-0000-0000-0000-000000000009', '-', ''))),
(26, UNHEX(REPLACE('00000000-0000-0000-0000-000000000010', '-', ''))),
-- Topic 27: NoSQL (students 11,12)
(27, UNHEX(REPLACE('00000000-0000-0000-0000-000000000011', '-', ''))),
(27, UNHEX(REPLACE('00000000-0000-0000-0000-000000000012', '-', '')));

-- ============================================================
-- SOLUTIONS (1-2 per task, from enrolled students)
-- ============================================================
INSERT INTO solutions (description, student_id, task_id) VALUES
-- Task 1 (topic 1, students 6,7,8) - 2 solutions
('Resolví el sistema usando el método de sustitución: x=2, y=3',                                    UNHEX(REPLACE('00000000-0000-0000-0000-000000000006', '-', '')), 1),
('Usé el método de Gauss-Jordan para resolver el sistema de ecuaciones',                             UNHEX(REPLACE('00000000-0000-0000-0000-000000000007', '-', '')), 1),
-- Task 2 (topic 1, students 6,7,8) - 2 solutions
('Factorización: x²+5x+6 = (x+2)(x+3)',                                                             UNHEX(REPLACE('00000000-0000-0000-0000-000000000007', '-', '')), 2),
('Apliqué la fórmula cuadrática para factorizar el polinomio',                                       UNHEX(REPLACE('00000000-0000-0000-0000-000000000008', '-', '')), 2),
-- Task 3 (topic 1, students 6,7,8) - 1 solution
('Simplifiqué la expresión encontrando el mínimo común denominador',                                 UNHEX(REPLACE('00000000-0000-0000-0000-000000000006', '-', '')), 3),
-- Task 4 (topic 2, students 6,9) - 2 solutions
('Calculé el área del triángulo usando la fórmula base por altura entre dos',                        UNHEX(REPLACE('00000000-0000-0000-0000-000000000006', '-', '')), 4),
('Usé la fórmula de Herón para calcular el área del triángulo escaleno',                             UNHEX(REPLACE('00000000-0000-0000-0000-000000000009', '-', '')), 4),
-- Task 5 (topic 2, students 6,9) - 1 solution
('Demostré el teorema usando triángulos semejantes y proporciones',                                  UNHEX(REPLACE('00000000-0000-0000-0000-000000000009', '-', '')), 5),
-- Task 6 (topic 3, students 7,8,10) - 2 solutions
('Calculé el límite aplicando la regla de L''Hôpital',                                              UNHEX(REPLACE('00000000-0000-0000-0000-000000000007', '-', '')), 6),
('Usé la factorización para resolver la indeterminación del límite',                                 UNHEX(REPLACE('00000000-0000-0000-0000-000000000008', '-', '')), 6),
-- Task 7 (topic 3, students 7,8,10) - 2 solutions
('Derivé f(g(x)) aplicando la regla de la cadena paso a paso',                                       UNHEX(REPLACE('00000000-0000-0000-0000-000000000008', '-', '')), 7),
('Apliqué la regla de la cadena a funciones trigonométricas compuestas',                             UNHEX(REPLACE('00000000-0000-0000-0000-000000000010', '-', '')), 7),
-- Task 8 (topic 3, students 7,8,10) - 1 solution
('Calculé la integral definida usando el teorema fundamental del cálculo',                           UNHEX(REPLACE('00000000-0000-0000-0000-000000000007', '-', '')), 8),
-- Task 9 (topic 4, students 9,11) - 2 solutions
('Apliqué F=ma para calcular la aceleración del bloque en el plano inclinado',                       UNHEX(REPLACE('00000000-0000-0000-0000-000000000009', '-', '')), 9),
('Resolví el problema usando las tres leyes de Newton y diagramas de cuerpo libre',                  UNHEX(REPLACE('00000000-0000-0000-0000-000000000011', '-', '')), 9),
-- Task 10 (topic 4, students 9,11) - 1 solution
('Calculé el trabajo como W=F·d·cos(θ) y la energía cinética como Ec=½mv²',                        UNHEX(REPLACE('00000000-0000-0000-0000-000000000009', '-', '')), 10),
-- Task 11 (topic 5, students 10,11,12) - 2 solutions
('Apliqué ΔU = Q - W para analizar el proceso termodinámico',                                        UNHEX(REPLACE('00000000-0000-0000-0000-000000000010', '-', '')), 11),
('Calculé el calor intercambiado usando el primer principio en un ciclo cerrado',                    UNHEX(REPLACE('00000000-0000-0000-0000-000000000011', '-', '')), 11),
-- Task 12 (topic 5, students 10,11,12) - 2 solutions
('Calculé la eficiencia del ciclo de Carnot como η = 1 - Tc/Th',                                    UNHEX(REPLACE('00000000-0000-0000-0000-000000000011', '-', '')), 12),
('Analicé el ciclo Otto y calculé su eficiencia térmica',                                            UNHEX(REPLACE('00000000-0000-0000-0000-000000000012', '-', '')), 12),
-- Task 13 (topic 5, students 10,11,12) - 1 solution
('Describí las diferencias entre procesos isotérmicos e isobáricos con gráficas PV',                 UNHEX(REPLACE('00000000-0000-0000-0000-000000000010', '-', '')), 13),
-- Task 14 (topic 6, students 12,13) - 2 solutions
('Identifiqué los grupos funcionales: alcohol, cetona y éster en las moléculas dadas',               UNHEX(REPLACE('00000000-0000-0000-0000-000000000012', '-', '')), 14),
('Clasifiqué los grupos funcionales usando espectroscopía IR como referencia',                       UNHEX(REPLACE('00000000-0000-0000-0000-000000000013', '-', '')), 14),
-- Task 15 (topic 6, students 12,13) - 1 solution
('Nombré los compuestos siguiendo las reglas IUPAC para alcanos y alquenos',                         UNHEX(REPLACE('00000000-0000-0000-0000-000000000012', '-', '')), 15),
-- Task 16 (topic 7, students 13,14,15) - 2 solutions
('Clasifiqué los compuestos inorgánicos en óxidos básicos, ácidos, hidróxidos y sales',              UNHEX(REPLACE('00000000-0000-0000-0000-000000000013', '-', '')), 16),
('Elaboré una tabla con ejemplos de cada tipo de compuesto inorgánico',                              UNHEX(REPLACE('00000000-0000-0000-0000-000000000014', '-', '')), 16),
-- Task 17 (topic 7, students 13,14,15) - 2 solutions
('Balanceé la reacción redox usando el método del ion-electrón',                                     UNHEX(REPLACE('00000000-0000-0000-0000-000000000014', '-', '')), 17),
('Apliqué el método de cambio de número de oxidación para balancear la reacción',                    UNHEX(REPLACE('00000000-0000-0000-0000-000000000015', '-', '')), 17),
-- Task 18 (topic 7, students 13,14,15) - 1 solution
('Calculé el pH usando la concentración de H⁺ y la fórmula pH = -log[H⁺]',                         UNHEX(REPLACE('00000000-0000-0000-0000-000000000013', '-', '')), 18),
-- Task 19 (topic 8, students 14,16) - 2 solutions
('Calculé la masa molar del NaCl y el número de moles en 58.5g',                                     UNHEX(REPLACE('00000000-0000-0000-0000-000000000014', '-', '')), 19),
('Usé el número de Avogadro para calcular el número de moléculas en la muestra',                     UNHEX(REPLACE('00000000-0000-0000-0000-000000000016', '-', '')), 19),
-- Task 20 (topic 8, students 14,16) - 1 solution
('Identifiqué el reactivo limitante comparando los moles disponibles con los estequiométricos',      UNHEX(REPLACE('00000000-0000-0000-0000-000000000014', '-', '')), 20),
-- Task 21 (topic 9, students 15,16,17) - 2 solutions
('Describí Mesopotamia, Egipto y las civilizaciones del Indo como las más importantes',              UNHEX(REPLACE('00000000-0000-0000-0000-000000000015', '-', '')), 21),
('Comparé las civilizaciones mesopotámicas destacando sus diferencias culturales',                   UNHEX(REPLACE('00000000-0000-0000-0000-000000000016', '-', '')), 21),
-- Task 22 (topic 9, students 15,16,17) - 2 solutions
('Analicé la democracia ateniense y la oligarquía espartana como modelos políticos',                 UNHEX(REPLACE('00000000-0000-0000-0000-000000000016', '-', '')), 22),
('Expliqué el sistema de polis griegas y su influencia en la política occidental',                   UNHEX(REPLACE('00000000-0000-0000-0000-000000000017', '-', '')), 22),
-- Task 23 (topic 9, students 15,16,17) - 1 solution
('Comparé ambos imperios en extensión, organización y legado histórico',                             UNHEX(REPLACE('00000000-0000-0000-0000-000000000015', '-', '')), 23),
-- Task 24 (topic 10, students 17,18) - 2 solutions
('Expliqué las causas económicas, sociales y políticas de la Revolución Francesa',                   UNHEX(REPLACE('00000000-0000-0000-0000-000000000017', '-', '')), 24),
('Analicé el papel de la Ilustración como causa ideológica de la Revolución',                        UNHEX(REPLACE('00000000-0000-0000-0000-000000000018', '-', '')), 24),
-- Task 25 (topic 10, students 17,18) - 1 solution
('Describí los cambios económicos y sociales provocados por la industrialización',                   UNHEX(REPLACE('00000000-0000-0000-0000-000000000017', '-', '')), 25),
-- Task 26 (topic 11, students 18,19,20) - 2 solutions
('Identifiqué montañas, llanuras, mesetas y valles como principales tipos de relieve',               UNHEX(REPLACE('00000000-0000-0000-0000-000000000018', '-', '')), 26),
('Clasifiqué el relieve terrestre según su origen: tectónico, volcánico y erosivo',                  UNHEX(REPLACE('00000000-0000-0000-0000-000000000019', '-', '')), 26),
-- Task 27 (topic 11, students 18,19,20) - 2 solutions
('Clasifiqué los climas según la clasificación de Köppen con ejemplos de cada zona',                 UNHEX(REPLACE('00000000-0000-0000-0000-000000000019', '-', '')), 27),
('Elaboré un mapa climático mundial usando la clasificación de Köppen',                              UNHEX(REPLACE('00000000-0000-0000-0000-000000000020', '-', '')), 27),
-- Task 28 (topic 11, students 18,19,20) - 1 solution
('Analicé latitud, altitud, corrientes marinas y vientos como factores climáticos',                  UNHEX(REPLACE('00000000-0000-0000-0000-000000000018', '-', '')), 28),
-- Task 29 (topic 12, students 6,19) - 2 solutions
('Analicé los factores que explican la concentración de población en ciertas regiones',              UNHEX(REPLACE('00000000-0000-0000-0000-000000000006', '-', '')), 29),
('Estudié la distribución desigual de la población usando mapas de densidad',                        UNHEX(REPLACE('00000000-0000-0000-0000-000000000019', '-', '')), 29),
-- Task 30 (topic 12, students 6,19) - 1 solution
('Comparé el PIB, IDH y estructura económica de países desarrollados y en desarrollo',               UNHEX(REPLACE('00000000-0000-0000-0000-000000000006', '-', '')), 30),
-- Task 31 (topic 13, students 7,20) - 2 solutions
('Localicé ciudades usando latitud y longitud en un mapa mundi',                                     UNHEX(REPLACE('00000000-0000-0000-0000-000000000007', '-', '')), 31),
('Practiqué la lectura de coordenadas geográficas con ejercicios de localización',                   UNHEX(REPLACE('00000000-0000-0000-0000-000000000020', '-', '')), 31),
-- Task 32 (topic 13, students 7,20) - 1 solution
('Comparé Mercator, Robinson y proyección azimutal destacando ventajas y desventajas',               UNHEX(REPLACE('00000000-0000-0000-0000-000000000007', '-', '')), 32),
-- Task 33 (topic 14, students 8,9,10) - 2 solutions
('Analicé la morfología de palabras identificando prefijos, raíces y sufijos',                       UNHEX(REPLACE('00000000-0000-0000-0000-000000000008', '-', '')), 33),
('Clasifiqué las palabras según su estructura morfológica: simples, derivadas y compuestas',         UNHEX(REPLACE('00000000-0000-0000-0000-000000000009', '-', '')), 33),
-- Task 34 (topic 14, students 8,9,10) - 2 solutions
('Identifiqué sujeto, predicado y complementos en oraciones subordinadas complejas',                 UNHEX(REPLACE('00000000-0000-0000-0000-000000000009', '-', '')), 34),
('Realicé el análisis sintáctico completo de tres oraciones compuestas',                             UNHEX(REPLACE('00000000-0000-0000-0000-000000000010', '-', '')), 34),
-- Task 35 (topic 14, students 8,9,10) - 1 solution
('Clasifiqué las subordinadas en sustantivas, adjetivas y adverbiales con ejemplos',                 UNHEX(REPLACE('00000000-0000-0000-0000-000000000008', '-', '')), 35),
-- Task 36 (topic 15, students 11,12) - 2 solutions
('Analicé el subjetivismo, la libertad y la naturaleza como rasgos del Romanticismo',                UNHEX(REPLACE('00000000-0000-0000-0000-000000000011', '-', '')), 36),
('Comparé el Romanticismo español con el europeo destacando sus diferencias',                        UNHEX(REPLACE('00000000-0000-0000-0000-000000000012', '-', '')), 36),
-- Task 37 (topic 15, students 11,12) - 1 solution
('Comenté el texto analizando narrador, personajes, tiempo y espacio narrativo',                     UNHEX(REPLACE('00000000-0000-0000-0000-000000000011', '-', '')), 37),
-- Task 38 (topic 16, students 13,14,15) - 2 solutions
('I used present perfect for recent actions and simple past for completed ones',                     UNHEX(REPLACE('00000000-0000-0000-0000-000000000013', '-', '')), 38),
('I wrote 10 sentences contrasting present perfect and simple past correctly',                       UNHEX(REPLACE('00000000-0000-0000-0000-000000000014', '-', '')), 38),
-- Task 39 (topic 16, students 13,14,15) - 2 solutions
('I completed exercises on all three types of conditional sentences',                                UNHEX(REPLACE('00000000-0000-0000-0000-000000000014', '-', '')), 39),
('I wrote examples of each conditional type in real and hypothetical situations',                    UNHEX(REPLACE('00000000-0000-0000-0000-000000000015', '-', '')), 39),
-- Task 40 (topic 16, students 13,14,15) - 1 solution
('I transformed 15 active sentences into passive voice correctly',                                   UNHEX(REPLACE('00000000-0000-0000-0000-000000000013', '-', '')), 40),
-- Task 41 (topic 17, students 16,17) - 2 solutions
('I learned 20 collocations with make/do and used them in original sentences',                       UNHEX(REPLACE('00000000-0000-0000-0000-000000000016', '-', '')), 41),
('I created a mind map with common collocations grouped by topic',                                   UNHEX(REPLACE('00000000-0000-0000-0000-000000000017', '-', '')), 41),
-- Task 42 (topic 17, students 16,17) - 1 solution
('I identified and used 15 phrasal verbs with get, take and put in context',                         UNHEX(REPLACE('00000000-0000-0000-0000-000000000016', '-', '')), 42),
-- Task 43 (topic 18, students 18,19,20) - 2 solutions
('I wrote a formal complaint letter about a defective product following the correct format',         UNHEX(REPLACE('00000000-0000-0000-0000-000000000018', '-', '')), 43),
('I structured the complaint letter with opening, body paragraphs and closing correctly',            UNHEX(REPLACE('00000000-0000-0000-0000-000000000019', '-', '')), 43),
-- Task 44 (topic 18, students 18,19,20) - 2 solutions
('I wrote an argumentative essay about climate change with thesis and supporting arguments',          UNHEX(REPLACE('00000000-0000-0000-0000-000000000019', '-', '')), 44),
('I structured the essay with introduction, three body paragraphs and conclusion',                   UNHEX(REPLACE('00000000-0000-0000-0000-000000000020', '-', '')), 44),
-- Task 45 (topic 18, students 18,19,20) - 1 solution
('I wrote a report describing trends shown in a bar chart about population growth',                  UNHEX(REPLACE('00000000-0000-0000-0000-000000000018', '-', '')), 45),
-- Task 46 (topic 19, students 6,8) - 2 solutions
('J''ai accordé les adjectifs en genre et en nombre dans 20 phrases',                               UNHEX(REPLACE('00000000-0000-0000-0000-000000000006', '-', '')), 46),
('J''ai corrigé les erreurs d''accord dans un texte et expliqué les règles',                        UNHEX(REPLACE('00000000-0000-0000-0000-000000000008', '-', '')), 46),
-- Task 47 (topic 19, students 6,8) - 1 solution
('J''ai utilisé qui, que, dont et où dans des phrases complexes correctement',                      UNHEX(REPLACE('00000000-0000-0000-0000-000000000006', '-', '')), 47),
-- Task 48 (topic 20, students 7,9,11) - 2 solutions
('J''ai conjugué 15 verbes au subjonctif présent dans des phrases avec que',                        UNHEX(REPLACE('00000000-0000-0000-0000-000000000007', '-', '')), 48),
('J''ai écrit des phrases exprimant le doute et la volonté au subjonctif',                          UNHEX(REPLACE('00000000-0000-0000-0000-000000000009', '-', '')), 48),
-- Task 49 (topic 20, students 7,9,11) - 2 solutions
('J''ai utilisé le conditionnel passé pour exprimer des regrets dans 10 phrases',                   UNHEX(REPLACE('00000000-0000-0000-0000-000000000009', '-', '')), 49),
('J''ai écrit des phrases hypothétiques avec si + plus-que-parfait + conditionnel passé',           UNHEX(REPLACE('00000000-0000-0000-0000-000000000011', '-', '')), 49),
-- Task 50 (topic 20, students 7,9,11) - 1 solution
('J''ai conjugué être, avoir, faire et aller au passé composé correctement',                        UNHEX(REPLACE('00000000-0000-0000-0000-000000000007', '-', '')), 50),
-- Task 51 (topic 21, students 10,12) - 2 solutions
('Ich habe Adjektive im Nominativ, Akkusativ und Dativ korrekt dekliniert',                          UNHEX(REPLACE('00000000-0000-0000-0000-000000000010', '-', '')), 51),
('Ich habe die Deklination nach bestimmtem und unbestimmtem Artikel geübt',                          UNHEX(REPLACE('00000000-0000-0000-0000-000000000012', '-', '')), 51),
-- Task 52 (topic 21, students 10,12) - 1 solution
('Ich habe können, müssen, dürfen und wollen in Sätzen korrekt verwendet',                          UNHEX(REPLACE('00000000-0000-0000-0000-000000000010', '-', '')), 52),
-- Task 53 (topic 22, students 13,15,17) - 2 solutions
('Ich habe 20 zusammengesetzte Substantive gelernt und ihre Bedeutung erklärt',                     UNHEX(REPLACE('00000000-0000-0000-0000-000000000013', '-', '')), 53),
('Ich habe neue Komposita aus bekannten Wörtern gebildet und verwendet',                             UNHEX(REPLACE('00000000-0000-0000-0000-000000000015', '-', '')), 53),
-- Task 54 (topic 22, students 13,15,17) - 2 solutions
('Ich habe Präpositionen mit Dativ (mit, bei, von) in Sätzen korrekt verwendet',                    UNHEX(REPLACE('00000000-0000-0000-0000-000000000015', '-', '')), 54),
('Ich habe Wechselpräpositionen mit Dativ und Akkusativ in Übungen geübt',                          UNHEX(REPLACE('00000000-0000-0000-0000-000000000017', '-', '')), 54),
-- Task 55 (topic 22, students 13,15,17) - 1 solution
('Ich habe Synonyme und Antonyme für 30 häufige deutsche Wörter gefunden',                          UNHEX(REPLACE('00000000-0000-0000-0000-000000000013', '-', '')), 55),
-- Task 56 (topic 23, students 14,16) - 2 solutions
('Ich habe die deutschen Vokale ä, ö, ü und die Umlaute korrekt ausgesprochen',                     UNHEX(REPLACE('00000000-0000-0000-0000-000000000014', '-', '')), 56),
('Ich habe Minimalpaare geübt um lange und kurze Vokale zu unterscheiden',                          UNHEX(REPLACE('00000000-0000-0000-0000-000000000016', '-', '')), 56),
-- Task 57 (topic 23, students 14,16) - 1 solution
('Ich habe die Satzmelodie bei Fragen und Aussagen im Deutschen geübt',                             UNHEX(REPLACE('00000000-0000-0000-0000-000000000014', '-', '')), 57),
-- Task 58 (topic 24, students 18,19,20) - 2 solutions
('Implementé una jerarquía de clases Animal-Perro-Gato con polimorfismo en Java',                   UNHEX(REPLACE('00000000-0000-0000-0000-000000000018', '-', '')), 58),
('Creé clases abstractas e interfaces para demostrar herencia múltiple en Java',                    UNHEX(REPLACE('00000000-0000-0000-0000-000000000019', '-', '')), 58),
-- Task 59 (topic 24, students 18,19,20) - 2 solutions
('Usé ArrayList, HashMap y Stream API para procesar una lista de empleados',                        UNHEX(REPLACE('00000000-0000-0000-0000-000000000019', '-', '')), 59),
('Implementé operaciones de filtrado y agrupación con Streams y Collectors',                        UNHEX(REPLACE('00000000-0000-0000-0000-000000000020', '-', '')), 59),
-- Task 60 (topic 24, students 18,19,20) - 1 solution
('Implementé Singleton con doble verificación y Factory Method para crear objetos',                 UNHEX(REPLACE('00000000-0000-0000-0000-000000000018', '-', '')), 60),
-- Task 61 (topic 25, students 6,7) - 2 solutions
('Creé decoradores para medir el tiempo de ejecución y cachear resultados en Python',               UNHEX(REPLACE('00000000-0000-0000-0000-000000000006', '-', '')), 61),
('Implementé decoradores con functools.wraps para preservar metadatos de funciones',                UNHEX(REPLACE('00000000-0000-0000-0000-000000000007', '-', '')), 61),
-- Task 62 (topic 25, students 6,7) - 1 solution
('Implementé una API REST con Flask con endpoints GET, POST, PUT y DELETE',                         UNHEX(REPLACE('00000000-0000-0000-0000-000000000006', '-', '')), 62),
-- Task 63 (topic 26, students 8,9,10) - 2 solutions
('Escribí consultas con INNER JOIN, LEFT JOIN y RIGHT JOIN entre 3 tablas',                         UNHEX(REPLACE('00000000-0000-0000-0000-000000000008', '-', '')), 63),
('Usé JOINs múltiples con subconsultas para obtener informes complejos',                            UNHEX(REPLACE('00000000-0000-0000-0000-000000000009', '-', '')), 63),
-- Task 64 (topic 26, students 8,9,10) - 2 solutions
('Creé un procedimiento almacenado para calcular el salario neto de empleados',                     UNHEX(REPLACE('00000000-0000-0000-0000-000000000009', '-', '')), 64),
('Implementé un trigger AFTER INSERT para registrar cambios en una tabla de auditoría',             UNHEX(REPLACE('00000000-0000-0000-0000-000000000010', '-', '')), 64),
-- Task 65 (topic 26, students 8,9,10) - 1 solution
('Creé índices compuestos y analicé el plan de ejecución con EXPLAIN',                              UNHEX(REPLACE('00000000-0000-0000-0000-000000000008', '-', '')), 65),
-- Task 66 (topic 27, students 11,12) - 2 solutions
('Diseñé un esquema de documentos para una tienda online con productos y pedidos',                  UNHEX(REPLACE('00000000-0000-0000-0000-000000000011', '-', '')), 66),
('Modelé documentos embebidos vs referencias en MongoDB para optimizar consultas',                  UNHEX(REPLACE('00000000-0000-0000-0000-000000000012', '-', '')), 66),
-- Task 67 (topic 27, students 11,12) - 1 solution
('Implementé operaciones CRUD en Redis usando strings, hashes y listas',                            UNHEX(REPLACE('00000000-0000-0000-0000-000000000011', '-', '')), 67);
