# Architecture SI Expert

## Vue d'ensemble

Le projet suit une architecture web classique séparée:

- un frontend React pour les écrans expert et admin
- un backend Spring Boot pour l'API et la logique métier
- Keycloak comme fournisseur d'identité
- une base relationnelle pour les données métier
- un stockage fichier local pour les pièces jointes
- un canal WebSocket pour les notifications temps réel

## Composants

### Frontend React

Dossier: `frontend/src`

Principaux composants:

- `App.js`: aiguillage global des écrans
- `components/Dashboard.js`: dashboard expert et corbeilles de missions
- `components/ExpertMissionDetail.js`: traitement détaillé d'une mission
- `components/AdminDashboard.js`: supervision admin, missions, experts, utilisateurs
- `components/NotificationBell.js`: centre de notifications temps réel
- `context/AuthContext.js`: session utilisateur Keycloak
- `services/axios.js`: client API avec bearer token
- `services/keycloak.js`: initialisation et opérations Keycloak

### Backend Spring Boot

Dossier: `src/main/java/com/wafa/assurance`

Couches principales:

- `controller/`: endpoints REST
- `service/`: logique métier et orchestration
- `repository/`: accès JPA
- `model/`: entités métier
- `dto/`: contrats d'échange API
- `config/`: sécurité, websocket, propriétés, seeding

## Authentification et sécurité

### Flux

1. Le frontend initialise Keycloak.
2. L'utilisateur se connecte sur Keycloak.
3. Le frontend récupère un access token.
4. `axios` ajoute `Authorization: Bearer ...` à chaque appel API.
5. `SecurityConfig` valide le JWT via `issuer-uri`.
6. Les rôles Keycloak sont convertis en autorités Spring `ROLE_ADMIN` et `ROLE_EXPERT`.

### Points clés

- Le backend ne gère plus le login métier applicatif.
- `GET /api/auth/me` retourne l'identité courante.
- Les comptes admin et expert restent synchronisés dans la table `users` pour les besoins métier.

## Domaine mission

### Entité Mission

La mission centralise:

- référence sinistre, police, immatriculation, parcours, téléphone assuré
- statut métier
- expert affecté
- dates de création, affectation, clôture, réouverture
- état de carence et durée de carence
- motif de refus et observations

### Statuts actuellement utilisés

- `NOUVELLE`
- `ACCEPTEE`
- `NON_CLOTUREE`
- `EN_COURS`
- `CARENCE`
- `HONORAIRES`
- `REFUSEE`
- `CLOTUREE`
- `REEXAMEN`

Le système garde certains statuts historiques pour rester compatible avec l'UI existante.

### Machine à états

Service principal: `MissionStateMachineService`

Transitions gérées:

- `NOUVELLE -> NON_CLOTUREE` lors de l'acceptation expert
- `NOUVELLE -> REFUSEE` lors du refus expert
- `NON_CLOTUREE -> EN_COURS` lors du traitement effectif
- `NON_CLOTUREE/EN_COURS -> CARENCE` via le job planifié
- `EN_COURS/CARENCE/HONORAIRES -> CLOTUREE`
- `CLOTUREE -> REEXAMEN` lors d'une réouverture admin
- `REFUSEE -> NOUVELLE` lors d'une réaffectation admin

Chaque transition est journalisée dans `mission_transitions`.

## Carence

### Objectif

Identifier automatiquement les missions affectées mais non traitées après un délai configurable.

### Implémentation

- Propriété: `app.carence.threshold-hours`
- Tâche planifiée: `MissionCarenceScheduler`
- Fréquence: toutes les heures
- Cible: missions affectées, non clôturées, non déjà en carence

À la détection:

- `estEnCarence = true`
- `dateCarence` et `dureeCarenceHeures` sont renseignées
- une transition vers `CARENCE` est créée
- une notification temps réel est émise

### API

- `GET /api/missions/corbeille/carence`
- `POST /api/missions/{id}/sortir-carence`

## Refus et réaffectation

### Historisation du refus

Entité: `MissionRefus`

Champs:

- mission
- expert
- motif normalisé `MotifRefus`
- commentaire
- date de refus

### Réaffectation

Un admin peut réaffecter une mission refusée via:

- `GET /api/admin/missions/refusees`
- `POST /api/admin/missions/{id}/reaffecter`

La mission revient alors dans un état de relance et l'expert cible est mis à jour.

## Réouverture

Entité: `MissionReouverture`

Une mission clôturée peut être rouverte seulement par un admin avec motif obligatoire.

API:

- `POST /api/admin/missions/{id}/rouvrir`

Effet:

- effacement de la date de clôture
- passage en `REEXAMEN`
- traçabilité complète dans l'historique

## Expertise et VVADE

### Entité Expertise

L'expertise contient:

- planning et localisation d'expertise
- kilométrage
- état général et état véhicule
- montant d'estimation
- entretien et carnet d'entretien
- cote Argus
- options spécifiques
- sinistres antérieurs
- VVADE calculée
- arbitrage, contre-expertise, rapport final

### Calcul VVADE

Service: `VvadeCalculator`

API:

- `POST /api/missions/{missionId}/expertise/calculer-vvade`

Principe:

- base Argus fournie
- coefficients kilométrage et âge
- coefficient d'état général
- coefficient lié au carnet d'entretien
- ajustement si sinistres antérieurs

Le résultat est renvoyé au frontend et persistant dans l'expertise la plus récente de la mission.

## Notifications

### Canal actuel

- stockage mémoire dans `NotificationCenterService`
- diffusion temps réel via WebSocket natif sur `/ws/notifications`
- affichage frontend dans `NotificationBell.js`

### Limites actuelles

- pas de persistance base pour les notifications
- pas de pipeline email industrialisé
- pas de STOMP/SockJS, seulement WebSocket direct

## Fichiers et uploads

Le stockage reste local sous `uploads/`.

Usages actuels:

- photos de mission
- pièces d'expertise
- notes d'honoraires
- factures

Les règles de centralisation, versioning et visibilité fine par rôle ne sont pas encore unifiées dans une entité unique `PieceJointe`.

## Administration

Le dashboard admin couvre aujourd'hui:

- vue de synthèse KPI
- supervision des missions
- supervision des experts
- consultation des sinistres dérivés des missions
- exports de rapports existants
- configuration d'affichage
- gestion des utilisateurs via Keycloak
- file de missions refusées à réaffecter

## Dette technique et zones encore incomplètes

Les points suivants restent partiels par rapport à une architecture cible complète:

- notifications email non branchées
- sinistre comme entité dédiée encore absent, les vues sinistre restent dérivées des missions
- génération de rapports administratifs avancés encore limitée aux exports existants
- gouvernance unifiée des pièces jointes non finalisée
- dashboard admin enrichi par graphiques dédiés encore limité

## Références utiles

- `README.md`: installation et usage courant
- `AUTHENTICATION.md`: détails Keycloak
- `ENV_SETUP.md`: variables d'environnement
