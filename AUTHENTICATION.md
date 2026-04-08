# Authentification SI Expert

## Configuration

### Frontend (React)

L'authentification est gérée par un contexte React(`AuthContext.js`) qui fournit:
- **login()** - Connexion avec email/mot de passe
- **logout()** - Déconnexion
- **register()** - Inscription d'un utilisateur
- **user** - Données de l'utilisateur connecté
- **loading** - État de chargement
- **error** - Messages d'erreur

#### Utilisation:
```jsx
import { useAuth } from '../context/AuthContext';

function MyComponent() {
  const { user, login, logout } = useAuth();
  // ...
}
```

### Service Axios
Le fichier `services/axios.js` configure une instance axios avec:
- Ajout automatique du token JWT dans le header `Authorization`
- Redirection automatique vers la page de login si le token expire (erreur 401)

### Page de Login
La page `components/Login.js` offre:
- Formulaire de connexion
- Formulaire d'inscription
- Affichage des erreurs
- Design moderne avec dégradé

## Backend (Spring Boot)

### Dépendances
- Spring Security
- JWT (jjwt)

### Configuration
Modifiez `application.yml`:
```yaml
jwt:
  secret: mysupersecretkeythathas32characterslong!!!  # À changer en production!
  expiration: 86400000  # 24 heures en ms
```

### Classes principales

#### AuthController
- `POST /api/auth/login` - Connexion
  - Body: `{ "email": "user@example.com", "password": "password" }`
  - Response: `{ "token": "JWT...", "user": { "id", "email", "nom", "prenom" } }`

- `POST /api/auth/register` - Inscription
  - Body: `{ "email", "password", "nom", "prenom" }`

#### Services
- **AuthService**: Gère login/register
- **JwtService**: Gère la génération et validation du JWT

#### Security
- **JwtAuthenticationFilter**: Vérifie le token JWT à chaque requête
- **SecurityConfig**: Configure Spring Security avec:
  - CORS activé pour `http://localhost:3000`
  - CSRF désactivé pour l'API
  - Sessions stateless (JWT)
  - Endpoints `/api/auth/**` publics
  - Autres endpoints protégés

## Flux d'authentification

```
1. Utilisateur se connecte via la page Login
   ↓
2. Appel POST /api/auth/login avec email/password
   ↓
3. Backend valide et génère un JWT
   ↓
4. Frontend stocke le token et l'utilisateur dans localStorage
   ↓
5. AuthContext met à jour l'état (user)
   ↓
6. App.js redisplay le Dashboard au lieu de Login
   ↓
7. Chaque requête API inclut: Authorization: Bearer {token}
   ↓
8. JwtAuthenticationFilter valide le token
   ↓
9. Si le token expire: erreur 401 → logout automatique
```

## Structure de la BD

```sql
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  nom VARCHAR(255) NOT NULL,
  prenom VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Points de sécurité importants

⚠️ **À faire en production:**
1. Changer `jwt.secret` par une clé très longue et aléatoire
2. Utiliser HTTPS (pas HTTP)
3. Stocker le JWT dans un cookie secure/httpOnly (pas localStorage)
4. Mettre en place une liste noire des tokens révoqués
5. Ajouter du rate limiting sur /api/auth/login
6. Ajouter une validation des emails (confirmation d'email)
7. Ajouter du captcha sur le formulaire d'inscription

## Test

### Inscription
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "nom": "Dupont",
    "prenom": "Jean"
  }'
```

### Connexion
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

### Utiliser le token
```bash
curl http://localhost:8080/api/missions/dashboard/compteurs \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```
