#!/bin/bash

# ============================================
# Script de Configuration de l'Environnement
# Pour Linux / macOS
# ============================================

echo "🔧 Initialisation de l'environnement SI-Expert..."
echo ""

# Vérifier si les fichiers .env existent
if [ ! -f .env ]; then
    echo "📝 Création de .env depuis .env.example..."
    cp .env.example .env
    echo "✅ .env créé - Veuillez éditer les valeurs"
else
    echo "✅ .env existe déjà"
fi

if [ ! -f frontend/.env ]; then
    echo "📝 Création de frontend/.env depuis frontend/.env.example..."
    cp frontend/.env.example frontend/.env
    echo "✅ frontend/.env créé - Veuillez éditer les valeurs"
else
    echo "✅ frontend/.env existe déjà"
fi

echo ""
echo "🔒 Vérification des permissions (fichiers .env privés)..."
chmod 600 .env 2>/dev/null || true
chmod 600 frontend/.env 2>/dev/null || true
echo "✅ Permissions configurées"

echo ""
echo "📦 Installation des dépendances..."

# Backend
echo "Backend - Installation Maven..."
./mvnw clean install -DskipTests 2>/dev/null || true

# Frontend
echo "Frontend - Installation npm..."
cd frontend
npm install
cd ..

echo ""
echo "✅ Configuration terminée!"
echo ""
echo "⚠️  IMPORTANT:"
echo "   1. Éditez .env avec vos configurations réelles"
echo "   2. Éditez frontend/.env avec vos configurations"
echo "   3. NE COMMITTEZ PAS les fichiers .env sur Git!"
echo ""
echo "🚀 Pour démarrer:"
echo "   Backend:  mvn spring-boot:run"
echo "   Frontend: cd frontend && npm start"
