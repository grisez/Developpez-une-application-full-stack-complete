-- Demo data, loaded with the dev profile only (see application-dev.properties), never in production.
-- Flyway runs this repeatable script again whenever its content changes: INSERT IGNORE and fixed ids
-- for posts and comments keep it safe to replay without creating duplicates.
-- Every demo account uses the password documented in the README, stored here as its Argon2id hash
-- (same parameters as the application: m=19456, t=2, p=1).

INSERT IGNORE INTO users (email, username, password, created_at, updated_at) VALUES
    ('alice@mdd.dev', 'alice', '$argon2id$v=19$m=19456,t=2,p=1$W7lET4a+skQB2D3xxOOMlw$Q7lDgX5NSWv0pbjzowtjwhUUUb4JmELNIs/NoFENubQ', '2026-10-01 09:00:00', '2026-10-01 09:00:00'),
    ('bob@mdd.dev', 'bob', '$argon2id$v=19$m=19456,t=2,p=1$W7lET4a+skQB2D3xxOOMlw$Q7lDgX5NSWv0pbjzowtjwhUUUb4JmELNIs/NoFENubQ', '2026-10-02 09:00:00', '2026-10-02 09:00:00'),
    ('carol@mdd.dev', 'carol', '$argon2id$v=19$m=19456,t=2,p=1$W7lET4a+skQB2D3xxOOMlw$Q7lDgX5NSWv0pbjzowtjwhUUUb4JmELNIs/NoFENubQ', '2026-10-03 09:00:00', '2026-10-03 09:00:00');

-- alice follows Java, Angular and Spring; bob follows Java, JavaScript and DevOps; carol follows nothing,
-- to show the empty feed and the invitation to subscribe
INSERT IGNORE INTO subscriptions (user_id, topic_id)
SELECT member.id, topic.id
FROM users member
JOIN topics topic
WHERE (member.username = 'alice' AND topic.name IN ('Java', 'Angular', 'Spring'))
   OR (member.username = 'bob' AND topic.name IN ('Java', 'JavaScript', 'DevOps'));

INSERT IGNORE INTO posts (id, title, content, created_at, author_id, topic_id) VALUES
    (1001, 'Les records Java en pratique',
     'Depuis Java 16, un record déclare une classe de données immuable en une ligne : constructeur, accesseurs, equals, hashCode et toString sont générés. Idéal pour les DTO.',
     '2026-10-10 09:15:00', (SELECT id FROM users WHERE username = 'bob'), (SELECT id FROM topics WHERE name = 'Java')),
    (1002, 'Les signals Angular expliqués simplement',
     'Un signal contient une valeur et prévient automatiquement ceux qui en dépendent quand elle change. Avec computed et effect, la réactivité devient plus lisible que de longues chaînes RxJS.',
     '2026-10-12 14:30:00', (SELECT id FROM users WHERE username = 'alice'), (SELECT id FROM topics WHERE name = 'Angular')),
    (1003, 'Injection par constructeur avec Spring',
     'Préférer l''injection par constructeur : les dépendances deviennent obligatoires et finales, et la classe se teste sans conteneur Spring.',
     '2026-10-14 08:00:00', (SELECT id FROM users WHERE username = 'alice'), (SELECT id FROM topics WHERE name = 'Spring')),
    (1004, 'Les promesses et async/await',
     'async/await rend le code asynchrone aussi lisible que du code synchrone. Pensez à try/catch pour gérer les erreurs des promesses rejetées.',
     '2026-10-16 17:45:00', (SELECT id FROM users WHERE username = 'bob'), (SELECT id FROM topics WHERE name = 'JavaScript')),
    (1005, 'Docker Compose pour le développement',
     'Un fichier docker-compose.yml décrit tous les services d''un projet. Un seul docker compose up suffit pour lancer la base de données, le back et le front.',
     '2026-10-18 10:20:00', (SELECT id FROM users WHERE username = 'carol'), (SELECT id FROM topics WHERE name = 'DevOps')),
    (1006, 'Streams Java : map, filter et toList',
     'Les streams décrivent un traitement de collection comme une suite d''étapes. Depuis Java 16, toList() remplace collect(Collectors.toList()) et renvoie une liste non modifiable.',
     '2026-10-20 11:00:00', (SELECT id FROM users WHERE username = 'carol'), (SELECT id FROM topics WHERE name = 'Java')),
    (1007, 'Valider ses formulaires Angular',
     'Les formulaires réactifs regroupent la valeur et les règles de validation dans le composant. Les mêmes règles doivent exister côté back : seul le serveur fait foi.',
     '2026-10-22 16:10:00', (SELECT id FROM users WHERE username = 'bob'), (SELECT id FROM topics WHERE name = 'Angular')),
    (1008, 'Pourquoi Python en data science',
     'Avec pandas, NumPy et scikit-learn, Python couvre toute la chaîne : préparation des données, analyse et apprentissage automatique.',
     '2026-10-23 09:30:00', (SELECT id FROM users WHERE username = 'carol'), (SELECT id FROM topics WHERE name = 'Python'));

INSERT IGNORE INTO comments (id, content, created_at, author_id, post_id) VALUES
    (2001, 'Très clair, j''utilise les records pour tous mes DTO maintenant.', '2026-10-10 12:00:00', (SELECT id FROM users WHERE username = 'alice'), 1001),
    (2002, 'Attention : un record ne convient pas pour une entité JPA.', '2026-10-10 15:30:00', (SELECT id FROM users WHERE username = 'carol'), 1001),
    (2003, 'Merci, le parallèle avec RxJS m''a aidé à comprendre.', '2026-10-12 18:00:00', (SELECT id FROM users WHERE username = 'bob'), 1002),
    (2004, 'Et Lombok avec @RequiredArgsConstructor rend ça encore plus court.', '2026-10-14 09:10:00', (SELECT id FROM users WHERE username = 'bob'), 1003),
    (2005, 'Je confirme, un docker compose up et tout tourne.', '2026-10-18 13:00:00', (SELECT id FROM users WHERE username = 'bob'), 1005),
    (2006, 'Bon rappel sur la validation côté serveur.', '2026-10-22 18:20:00', (SELECT id FROM users WHERE username = 'alice'), 1007);
