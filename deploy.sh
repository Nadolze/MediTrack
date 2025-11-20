PORT="$1"

if [ -z "$PORT" ]; then
  echo "Usage: $0 <PORT>"
  exit 1
fi

echo "Stopping old instance on port ${PORT} (timeout 5s)..."
PID=$(lsof -t -i:${PORT} || true)

if [ -n "$PID" ]; then
  kill $PID
  sleep 5
  if kill -0 $PID 2>/dev/null; then
    echo "Force killing PID $PID"
    kill -9 $PID
  fi
else
  echo "No process running on port ${PORT}"
fi

echo "Starting new instance on port ${PORT}..."
nohup java -jar target/meditrack-0.0.1-SNAPSHOT.jar --server.port=${PORT} > "app_${PORT}.log" 2>&1 &
echo "Deployment auf Port ${PORT} abgeschlossen."