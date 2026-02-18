
# Invoice App

Web App für Rechnungen, Kunden und Positionen. Frontend mit Vue 3 und Vite. Backend mit Spring Boot.

## Projektstruktur

1. backend/invoice-backend, Spring Boot API, Port 8080
2. frontend, Vue 3 App, Port 5173

## Voraussetzungen

1. Git
2. Java 21
3. Node.js 20 oder neuer
4. npm 10 oder neuer

Optional für das Start Script unter Windows

1. Git Bash

## Schnellstart

### 1) Projekt klonen

```bash
git clone https://github.com/Mirac61/InvoicePWA.git
cd invoice-app
```

### 2) Start mit einem Befehl

macOS oder Linux

```bash
chmod +x dev.sh
./dev.sh
```

Windows mit Git Bash

```bash
bash dev.sh
```

Danach öffnen

1. Frontend: http://localhost:5173
2. Backend Health: http://localhost:8080/api/health

## Manuell starten

### 1) Backend starten

```bash
cd backend/invoice-backend
./mvnw clean package
./mvnw spring-boot:run
```

Backend prüfen

1. http://localhost:8080/api/health

### 2) Frontend starten

Neues Terminal öffnen

```bash
cd frontend
npm install
npm run dev
```

Frontend öffnen

1. http://localhost:5173

## Verbindung Frontend zu Backend

Das Frontend ruft Endpunkte unter /api auf. Vite leitet /api an das Backend weiter.

Vite Proxy Einstellung findest du in

1. frontend/vite.config.ts oder frontend/vite.config.js

## Typische Probleme

### Port ist belegt

Wenn das Frontend nicht auf 5173 startet, zeigt Vite den neuen Port in der Konsole.

Du kannst auch fix starten

```bash
cd frontend
npm run dev -- --port 5173
```

### Backend startet nicht

Prüfe zuerst den Build

```bash
cd backend/invoice-backend
./mvnw clean package
```

Wenn das fehlschlägt, lies den ersten ERROR Block in der Konsole.

### Java Version passt nicht

Prüfen

```bash
java -version
```

Du brauchst Java 21.

### Node Version passt nicht

Prüfen

```bash
node -v
npm -v
```

Du brauchst Node 20 oder neuer.

### Frontend hat API Fehler, Backend läuft

1. Öffne http://localhost:8080/api/health
2. Prüfe die Proxy Einstellung in vite.config
3. Prüfe in der Browser Konsole, ob Calls auf /api gehen

## Nützliche Befehle

Backend Tests

```bash
cd backend/invoice-backend
./mvnw test
```

Frontend Build

```bash
cd frontend
npm run build
```

Frontend Preview

```bash
cd frontend
npm run preview
```

## Lizenz

Private Nutzung für dieses Projekt. 
