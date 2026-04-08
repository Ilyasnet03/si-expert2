# SI Expert - Guide d'administration

## Configuration actuelle

- **Backend**: Spring Boot 3.5.2 sur le port 8083
- **Base de données**: H2 en mémoire (développement)
- **Console H2**: http://localhost:8083/h2-console
- **Authentification**: JWT avec Spring Security

## Création manuelle de comptes utilisateurs

### Méthode 1 : Via la console H2 (Recommandée)

1. **Démarrer l'application** avec .\run.ps1
2. **Accéder à la console H2** : http://localhost:8083/h2-console
3. **Connexion à la base de données** :
   - JDBC URL: jdbc:h2:mem:testdb
   - Username: sa
   - Password: (laisser vide)
4. **Exécuter le script SQL** depuis create_admin.sql ou manuellement :

`sql
-- Créer un utilisateur administrateur
INSERT INTO users (email, password, nom, prenom, role, enabled, created_at)
VALUES ('admin@si-expert.com', '', 'Admin', 'Système', 'ADMIN', true, CURRENT_TIMESTAMP);

-- Créer un utilisateur expert
INSERT INTO users (email, password, nom, prenom, role, enabled, created_at)
VALUES ('expert@si-expert.com', '', 'Expert', 'Assurance', 'USER', true, CURRENT_TIMESTAMP);
`

### Comptes de test créés

- **Administrateur**: admin@si-expert.com / admin123
- **Expert**: expert@si-expert.com / admin123

### Génération de hash BCrypt

Pour générer un hash BCrypt pour un nouveau mot de passe, vous pouvez :

1. **Utiliser Spring Boot** :
`java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hashedPassword = encoder.encode("votre_mot_de_passe");
`

2. **Utiliser un outil en ligne** : Rechercher "BCrypt generator online"

### Rôles disponibles

- ADMIN : Accès complet à toutes les fonctionnalités
- USER : Accès aux missions d'expertise (rôle par défaut)

### Sécurité

- Les mots de passe doivent respecter la politique de sécurité de l'entreprise
- Utilisez des mots de passe forts et uniques
- Changez régulièrement les mots de passe par défaut
- Activez l'authentification à deux facteurs si disponible

### Démarrage de l'application

`powershell
# Depuis le répertoire racine du projet
.\run.ps1
`

L'application sera disponible sur :
- Backend API: http://localhost:8083
- Console H2: http://localhost:8083/h2-console

### Support

En cas de problème avec la création de comptes, contactez l'équipe de développement.
