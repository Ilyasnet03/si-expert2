# Authentification SI Expert

## Vue d'ensemble

L'application utilise maintenant Keycloak comme fournisseur d'identité.

### Frontend (React)

L'authentification est gérée par le contexte React dans `frontend/src/context/AuthContext.js` avec l'appui de `frontend/src/services/keycloak.js`.

- `login()` redirige vers Keycloak
- `logout()` ferme la session Keycloak
- `user` contient les informations extraites du token OpenID Connect
- `loading` reflète l'initialisation de la session Keycloak
- `error` expose les erreurs de configuration ou de session

### Service Axios

Le fichier `frontend/src/services/axios.js` ajoute automatiquement le bearer token Keycloak sur chaque requête API.

### Backend (Spring Boot)

Le backend agit comme `OAuth2 Resource Server` et valide les JWT émis par Keycloak via `issuer-uri`.

- `SecurityConfig` convertit les rôles Keycloak en autorités Spring `ROLE_ADMIN` et `ROLE_EXPERT`
- `GET /api/auth/me` permet de lire les informations utilisateur depuis le token courant
- `POST /api/auth/login` et `POST /api/auth/register` sont volontairement désactivés

## Démarrage local rapide

### Services Docker

Le projet fournit maintenant Keycloak dans [docker-compose.yml](docker-compose.yml).

```bash
docker compose up -d db keycloak
```

### Console Keycloak

- URL: `http://localhost:8081`
- Admin: `admin`
- Mot de passe: `admin`

### Realm importé automatiquement

Le realm de développement est importé depuis `keycloak/import/si-expert-realm.json`.

Configuration incluse:

- Realm: `si-expert`
- Client public: `si-expert-frontend`
- Redirect URI: `http://localhost:3000/*`
- Web origin: `http://localhost:3000`
- Rôles: `ADMIN`, `EXPERT`

### Utilisateurs de test

- `admin` / `admin123` avec rôle `ADMIN`
- `expert` / `expert123` avec rôle `EXPERT`

## Variables d'environnement

### Frontend

```env
REACT_APP_API_BASE_URL=http://localhost:8080/api
REACT_APP_KEYCLOAK_URL=http://localhost:8081
REACT_APP_KEYCLOAK_REALM=si-expert
REACT_APP_KEYCLOAK_CLIENT_ID=si-expert-frontend
```

### Backend

```env
FRONTEND_URL=http://localhost:3000
KEYCLOAK_ISSUER_URI=http://localhost:8081/realms/si-expert
```

## Flux d'authentification

```text
1. L'utilisateur ouvre l'application React
2. AuthContext initialise Keycloak avec check-sso
3. Si aucune session n'existe, le bouton de login redirige vers Keycloak
4. Keycloak authentifie l'utilisateur
5. Keycloak renvoie un access token au frontend
6. Axios envoie Authorization: Bearer <token>
7. Spring Security valide le JWT via l'issuer Keycloak
8. Les rôles du token protègent les endpoints API
```

## Test manuel

### Tester l'authentification complète

```bash
docker compose up -d db keycloak
./mvnw spring-boot:run
cd frontend
npm start
```

Ensuite:

1. Ouvrir `http://localhost:3000`
2. Cliquer sur `Se connecter avec Keycloak`
3. Utiliser `admin/admin123` ou `expert/expert123`

### Tester l'API avec un token

Après connexion, récupérez le token dans le navigateur puis utilisez:

```bash
curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer <token>"
```

## Différence avec l'ancien système

- Avant: le backend générait son propre JWT
- Maintenant: le token est émis par Keycloak
- Avant: les comptes étaient gérés dans l'application
- Maintenant: les utilisateurs et rôles sont gérés par Keycloak
- Avant: le login passait par `/api/auth/login`
- Maintenant: la connexion passe par la page de login Keycloak
