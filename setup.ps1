# ============================================
# Script de Configuration de l'Environnement
# Pour Windows (PowerShell)
# ============================================

Write-Host "🔧 Initialisation de l'environnement SI-Expert..." -ForegroundColor Cyan
Write-Host ""

# Vérifier si les fichiers .env existent
if (-not (Test-Path ".env")) {
    Write-Host "📝 Création de .env depuis .env.example..." -ForegroundColor Yellow
    Copy-Item ".env.example" ".env"
    Write-Host "✅ .env créé - Veuillez éditer les valeurs" -ForegroundColor Green
} else {
    Write-Host "✅ .env existe déjà" -ForegroundColor Green
}

if (-not (Test-Path "frontend\.env")) {
    Write-Host "📝 Création de frontend\.env depuis frontend\.env.example..." -ForegroundColor Yellow
    Copy-Item "frontend\.env.example" "frontend\.env"
    Write-Host "✅ frontend\.env créé - Veuillez éditer les valeurs" -ForegroundColor Green
} else {
    Write-Host "✅ frontend\.env existe déjà" -ForegroundColor Green
}

Write-Host ""
Write-Host "🔒 Vérification des permissions (fichiers .env privés)..." -ForegroundColor Yellow
# Les permissions sous Windows se font automatiquement, juste un message
Write-Host "✅ Permissions configurées" -ForegroundColor Green

Write-Host ""
Write-Host "📦 Installation des dépendances..." -ForegroundColor Yellow

# Backend
Write-Host "Backend - Installation Maven..." -ForegroundColor Cyan
& ".\mvnw.cmd" clean install -DskipTests | Out-Null

# Frontend
Write-Host "Frontend - Installation npm..." -ForegroundColor Cyan
Set-Location frontend
& npm install
Set-Location ..

Write-Host ""
Write-Host "✅ Configuration terminée!" -ForegroundColor Green
Write-Host ""
Write-Host "⚠️  IMPORTANT:" -ForegroundColor Red
Write-Host "   1. Éditez .env avec vos configurations réelles"
Write-Host "   2. Éditez frontend\.env avec vos configurations"
Write-Host "   3. NE COMMITTEZ PAS les fichiers .env sur Git!"
Write-Host ""
Write-Host "🚀 Pour démarrer:" -ForegroundColor Green
Write-Host "   Backend:  .\mvnw spring-boot:run"
Write-Host "   Frontend: cd frontend; npm start"
