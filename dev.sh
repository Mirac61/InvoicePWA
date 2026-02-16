#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="$ROOT_DIR/backend/invoice-backend"
FRONTEND_DIR="$ROOT_DIR/frontend"

BACKEND_PID=""
FRONTEND_PID=""

cleanup() {
  if [[ -n "$BACKEND_PID" ]] && kill -0 "$BACKEND_PID" 2>/dev/null; then
    kill "$BACKEND_PID" 2>/dev/null || true
  fi
  if [[ -n "$FRONTEND_PID" ]] && kill -0 "$FRONTEND_PID" 2>/dev/null; then
    kill "$FRONTEND_PID" 2>/dev/null || true
  fi
}

trap cleanup EXIT INT TERM

if [[ ! -d "$FRONTEND_DIR/node_modules" ]]; then
  echo "[setup] frontend/node_modules fehlt - fuehre npm install aus..."
  (cd "$FRONTEND_DIR" && npm install)
fi

echo "[start] Backend auf http://localhost:8080"
(
  cd "$BACKEND_DIR"
  ./mvnw -Dmaven.repo.local=./.m2 spring-boot:run > backend_startup.log 2>&1
) &
BACKEND_PID=$!

echo "[start] Frontend auf http://localhost:5173"
(
  cd "$FRONTEND_DIR"
  npm run dev
) &
FRONTEND_PID=$!

echo "[info] Beide Dienste laufen. Beenden mit Ctrl+C."

while true; do
  if ! kill -0 "$BACKEND_PID" 2>/dev/null; then
    echo "[stop] Backend wurde beendet."
    break
  fi

  if ! kill -0 "$FRONTEND_PID" 2>/dev/null; then
    echo "[stop] Frontend wurde beendet."
    break
  fi

  sleep 1
done
