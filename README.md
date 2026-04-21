# SI Expert

Application de gestion des missions d'expertise automobile avec frontend React, backend Spring Boot et authentification Keycloak.

## Fonctionnalités principales

- Authentification centralisée via Keycloak avec rôles `ADMIN` et `EXPERT`
- Dashboard expert avec corbeilles par statut
- Acceptation, refus, traitement, clôture et réouverture des missions
- Gestion de la carence avec seuil configurable et calcul planifié
- Historique des refus et réaffectation par un administrateur
- Timeline des transitions d'état d'une mission
- Gestion des expertises, devis, photos, honoraires et factures
- Calcul de la VVADE côté serveur
- Notifications temps réel via WebSocket
- Administration des utilisateurs synchronisée avec Keycloak

## Stack technique

- Frontend: React, axios, keycloak-js, jsPDF, lucide-react
- Backend: Spring Boot 3.5, Spring Web, Spring Data JPA, Spring Security, OAuth2 Resource Server, WebSocket
- Authentification: Keycloak
- Base de données locale par défaut: H2
- Base de données cible supportée: PostgreSQL

## Architecture rapide

- `frontend/`: application React
- `src/main/java/`: API Spring Boot et logique métier
- `src/main/resources/`: configuration, données d'initialisation, templates
- `keycloak/import/si-expert-realm.json`: realm de développement
- `uploads/`: fichiers métier déposés localement

Le document d'architecture détaillé est dans `ARCHITECTURE.md`.

## Authentification

Le frontend redirige vers Keycloak pour la connexion. Le backend valide ensuite les JWT en mode Resource Server.

Variables minimales:

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
KEYCLOAK_ADMIN_SERVER_URL=http://localhost:8081
KEYCLOAK_ADMIN_TARGET_REALM=si-expert
KEYCLOAK_ADMIN_AUTH_REALM=master
KEYCLOAK_ADMIN_CLIENT_ID=admin-cli
KEYCLOAK_ADMIN_USERNAME=admin
KEYCLOAK_ADMIN_PASSWORD=admin
APP_CARENCE_THRESHOLD_HOURS=48
```

## Démarrage local

### 1. Lancer les services techniques

```powershell
docker compose up -d db keycloak
```

### 2. Lancer le backend

```powershell
.\mvnw.cmd spring-boot:run
```

### 3. Lancer le frontend

```powershell
Set-Location frontend
npm install
npm start
```

## Build

### Backend

```powershell
.\mvnw.cmd -DskipTests compile
```

### Frontend

```powershell
Set-Location frontend
npm run build
```

## Workflow mission actuel

### Expert

- Une mission `NOUVELLE` peut être acceptée ou refusée.
- Lors de l'acceptation, la mission est affectée à l'expert connecté.
- Une mission affectée peut passer en `CARENCE` automatiquement si le seuil est dépassé.
- L'expert peut sortir une mission de carence, poursuivre le traitement, déposer les pièces métier puis clôturer.
- Une mission clôturée reste consultable en lecture seule.

### Admin

- Visualisation des missions et des refus enregistrés
- Réaffectation d'une mission refusée à un autre expert
- Réouverture d'une mission clôturée avec motif obligatoire, passage en `REEXAMEN`
- Gestion des comptes utilisateurs via Keycloak

## Carence

- Seuil configurable par `app.carence.threshold-hours`
- Job planifié toutes les heures
- Détection sur les missions affectées non clôturées
- Notification temps réel à la mise en carence
- Corbeille expert dédiée côté frontend

## VVADE

Le calcul est exposé côté backend via:

- `POST /api/missions/{missionId}/expertise/calculer-vvade`

Le calcul s'appuie sur:

- la cote Argus saisie
- le kilométrage
- l'âge du véhicule
- l'état général
- la présence du carnet d'entretien
- l'existence de sinistres antérieurs

## Notes sur l'état du projet

- Le canal email n'est pas encore industrialisé: les notifications applicatives sont actuellement temps réel via WebSocket et conservées en mémoire.
- La documentation historique `HELP.md` et une partie des anciens guides ne reflètent plus l'architecture réelle. Utiliser `README.md`, `ARCHITECTURE.md`, `AUTHENTICATION.md` et `ENV_SETUP.md` comme référence.
