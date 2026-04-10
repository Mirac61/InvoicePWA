# Invoice App

Web App für Rechnungen, Kunden und Positionen.
Frontend mit **Vue 3 + Vite + Tailwind CSS**. Backend mit **Spring Boot**.

-----

## Voraussetzungen

|Tool   |Version|
|-------|-------|
|Java   |21     |
|Node.js|20+    |
|npm    |10+    |


> **Windows?** Man braucht zusätzlich [Git Bash](https://gitforwindows.org/), um das Startscript auszuführen.

-----

## Schnellstart

### 1. Projekt klonen

```bash
git clone https://github.com/Mirac61/InvoicePWA.git
cd invoice-app
```

### 2. Alles mit einem Befehl starten

**macOS / Linux**

```bash
chmod +x dev.sh
./dev.sh
```

**Windows (Git Bash)**

```bash
bash dev.sh
```

Das Script startet Backend und Frontend automatisch.

### 3. App öffnen

|Dienst        |URL                             |
|--------------|--------------------------------|
|Frontend      |http://localhost:5173           |
|Backend Health|http://localhost:8080/api/health|

Wenn beide URLs antworten, läuft alles.

-----

## Manuell starten (ohne Script)

Falls das Script nicht funktioniert, kannst du beide Dienste einzeln starten.

**Terminal 1 – Backend**

```bash
cd backend/invoice-backend
./mvnw clean package
./mvnw spring-boot:run
```

**Terminal 2 – Frontend**

```bash
cd frontend
npm install
npm run dev
```

-----

## Tailwind CSS einrichten

Tailwind ist bereits als Abhängigkeit in der `package.json` eingetragen. Ein `npm install` reicht — du musst nichts extra installieren.

Damit Tailwind funktioniert, müssen **drei Dinge** stimmen:

### 1. Plugin in `vite.config.ts` eingetragen

```ts
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [
    vue(),
    tailwindcss(), // ← das hier
  ],
})
```

### 2. Tailwind in der globalen CSS-Datei importiert

```css
/* src/style.css */
@import "tailwindcss";
```

### 3. CSS-Datei in `main.ts` importiert

```ts
import './style.css'
```

Wenn alle drei Punkte stimmen, können Tailwind-Klassen direkt in Vue-Templates verwendet werden:

```vue
<button class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700">
  Speichern
</button>
```

-----

## Projektstruktur

```
invoice-app/
├── backend/
│   └── invoice-backend/   ← Spring Boot API (Port 8080)
└── frontend/              ← Vue 3 App (Port 5173)
    ├── src/
    │   └── style.css      ← Tailwind Import hier
    └── vite.config.ts     ← Tailwind Plugin hier
```

-----

## Häufige Probleme

### Tailwind-Klassen werden nicht angezeigt

Checke die drei Punkte aus dem Tailwind-Abschnitt oben. Danach Dev-Server neu starten:

```bash
# Ctrl+C, dann:
npm run dev
```

### Port 5173 ist belegt

Vite zeigt in der Konsole automatisch einen freien Port. Oder fix auf 5173 erzwingen:

```bash
npm run dev -- --port 5173
```

### Backend startet nicht

```bash
cd backend/invoice-backend
./mvnw clean package
```

Den ersten `ERROR`-Block in der Konsole lesen — dort steht die eigentliche Ursache.

### Java oder Node Version stimmt nicht

```bash
java -version   # braucht Java 21
node -v         # braucht Node 20+
npm -v          # braucht npm 10+
```

### Frontend zeigt API-Fehler, Backend läuft aber

1. http://localhost:8080/api/health aufrufen — kommt eine Antwort?
1. Proxy-Einstellung in `frontend/vite.config.ts` prüfen
1. In der Browser-Konsole schauen, ob die Requests auf `/api` gehen

-----

## Nützliche Befehle

```bash
# Backend Tests
cd backend/invoice-backend && ./mvnw test

# Frontend für Produktion bauen
cd frontend && npm run build

# Produktions-Build lokal testen
cd frontend && npm run preview
```

-----

## Lizenz

Private Nutzung.

