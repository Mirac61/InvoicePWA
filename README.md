# Invoice App (Vue.js + Spring Boot)

Dieses Projekt ist jetzt bewusst einfach aufgebaut:

- `backend/invoice-backend`: Spring Boot API (Port `8080`)
- `frontend`: Vue 3 + Vite (Port `5173`)

## Voraussetzungen

- Java 21
- Node.js 20+
- npm 10+

## Ein Befehl fuer alles

```bash
cd /Users/mirac/invoice-app
./dev.sh
```

Dann oeffnen:

- http://localhost:5173

## Manuell (optional)

## 1) Backend starten

```bash
cd backend/invoice-backend
./mvnw spring-boot:run
```

Test im Browser:

- http://localhost:8080/api/health

## 2) Frontend starten

Neues Terminal:

```bash
cd frontend
npm install
npm run dev
```

Dann öffnen:

- http://localhost:5173

## Troubleshooting

- Wenn Maven wegen Rechten auf `~/.m2` fehlschlägt:

```bash
cd backend/invoice-backend
./mvnw -Dmaven.repo.local=./.m2 spring-boot:run
```

- Wenn `npm install` oder Maven keine Pakete laden können, fehlt Netzwerkzugriff in deiner Umgebung.
- `dev.sh` nutzt bereits `-Dmaven.repo.local=./.m2`, damit kein Schreibzugriff auf `~/.m2` noetig ist.

## Wie Frontend und Backend verbunden sind

- Das Vue-Frontend ruft `/api/health` auf.
- Vite leitet `/api/*` automatisch an `http://localhost:8080` weiter (`frontend/vite.config.js`).

## Nächste sinnvolle Schritte

1. Invoice- und Customer-Modelle als JPA-Entities hinzufügen.
2. REST-Endpunkte für `GET/POST /api/invoices` bauen.
3. Vue-Ansichten für Liste + Formular bauen.
4. Später PDF-Export und einfache Auth ergänzen.
