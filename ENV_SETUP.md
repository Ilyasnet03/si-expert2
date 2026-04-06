# 🔐 Configuration des Variables d'Environnement

Ce projet utilise des fichiers `.env` pour gérer les configurations sensibles (mots de passe, clés d'API, etc.) et ne jamais les exposer sur GitHub.

## 📋 Fichiers de Configuration

### Backend (Racine du projet)

#### `.env` (NE PAS COMMITER)
- Fichier principal contenant toutes les variables d'environnement sensibles
- **JAMAIS à commiter sur Git** (ignoré par `.gitignore`)
- À créer localement en copiant `.env.example`

#### `.env.example` (À commiter)
- Modèle de configuration sans valeurs sensibles
- Utilisé comme référence pour créer `.env`
- Contient tous les paramètres nécessaires avec des valeurs placeholder

### Frontend (`frontend/`)

#### `frontend/.env` (NE PAS COMMITER)
- Fichier contenant les variables React
- **JAMAIS à commiter sur Git**
- À créer localement en copiant `frontend/.env.example`

#### `frontend/.env.example` (À commiter)
- Modèle de configuration pour le frontend
- Contient les paramètres React avec valeurs placeholder

## 🚀 Installation et Configuration

### 1. Backend

```bash
# Copier le fichier exemple
cp .env.example .env

# Éditer .env et remplir les valeurs réelles
# Exemple:
# DB_PASSWORD=votre_mot_de_passe_reel
# RAPIDAPI_KEY=votre_vraie_cle_api
```

### 2. Frontend

```bash
# Entrer dans le répertoire frontend
cd frontend

# Copier le fichier exemple
cp .env.example .env

# Éditer .env avec les bonnes valeurs
# REACT_APP_API_URL=http://localhost:8080/api
```

## 📝 Variables Disponibles

### Base de Données
```
DB_HOST=localhost
DB_PORT=5432
DB_NAME=si_expert_db
DB_USER=postgres
DB_PASSWORD=votre_mot_de_passe
```

### Serveur
```
SERVER_PORT=8080
SPRING_PROFILE=dev
```

### APIs Externes
```
RAPIDAPI_KEY=votre_cle_api
RAPIDAPI_HOST=tecdoc-catalog.p.rapidapi.com
```

### Frontend React
```
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_ENV=development
REACT_APP_DEBUG=true
```

### Uploads
```
UPLOAD_DIR=./uploads
MAX_FILE_SIZE=10485760
```

## ✅ Vérification

### Avant de commiter sur Git

```bash
# Vérifier que .env n'est pas tracé
git status

# La sortie ne doit PAS contenir .env ou frontend/.env

# Vérifier le .gitignore
cat .gitignore | grep -E "^\.env"
```

## 🔒 Bonnes Pratiques

1. **JAMAIS** ne commiter les fichiers `.env` contenant les valeurs réelles
2. **TOUJOURS** maintenir les fichiers `.env.example` à jour avec la nouvelle structure
3. **DOCUMENTER** chaque nouvelle variable dans `.env.example`
4. **UTILISER** des valeurs placeholders dans `.env.example` (ex: `votre_cle_api_ici`)
5. **PROTÉGER** les fichiers `.env` en local (fichier système privé)

## 🛠️ Dépannage

### Spring Boot ne démarre pas
- Vérifier que `.env` existe et est au bon endroit
- Vérifier les permissions du fichier
- Vérifier la syntaxe du `.env` (pas d'espaces autour du `=`)

### React ne se connecte pas à l'API
- Vérifier que `REACT_APP_API_URL` est correct dans `frontend/.env`
- Rebuild le frontend après modification: `npm run build`

### Variables non lues
- Les variables d'environnement ne sont lues qu'au démarrage
- Redémarrer le serveur/l'application après modification du `.env`

---

**Créé le 06/04/2026** - Version 1.0
