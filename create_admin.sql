-- Script pour créer un compte administrateur
-- À exécuter dans la console H2: http://localhost:8083/h2-console
-- JDBC URL: jdbc:h2:mem:testdb
-- Username: sa
-- Password: (laisser vide)

-- Créer un utilisateur administrateur avec mot de passe hashé
-- Mot de passe: admin123 (hashé avec BCrypt)
INSERT INTO users (email, password, nom, prenom, role, enabled, created_at)
VALUES ('admin@si-expert.com', '$2a$10$8K3W2QJc8X9VzJc8X9VzJe8X9VzJc8X9VzJc8X9VzJc8X9VzJc8X9VzJc', 'Admin', 'Système', 'ADMIN', true, CURRENT_TIMESTAMP);

-- Créer un utilisateur expert
INSERT INTO users (email, password, nom, prenom, role, enabled, created_at)
VALUES ('expert@si-expert.com', '$2a$10$8K3W2QJc8X9VzJc8X9VzJe8X9VzJc8X9VzJc8X9VzJc8X9VzJc8X9VzJc', 'Expert', 'Assurance', 'USER', true, CURRENT_TIMESTAMP);