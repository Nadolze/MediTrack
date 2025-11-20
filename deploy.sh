#!/bin/bash
PORT=$1
LOG_FILE="app_${PORT}.log"

echo "Stopping old instance on port ${PORT}..."
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
# Spring Boot direkt in Jenkins ausgeben, kein &!
java -jar target/meditrack-0.0.1-SNAPSHOT.jar --server.port=${PORT} 2>&1 | tee ${LOG_FILE}
