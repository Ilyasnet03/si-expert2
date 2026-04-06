$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "JAVA_HOME : $env:JAVA_HOME" -ForegroundColor Green
Write-Host "Version Java :" -ForegroundColor Yellow
java -version
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Démarrage de l'application..." -ForegroundColor Yellow

.\mvnw spring-boot:run
