#!/bin/bash

PORT=$1
JAR_FILE="target/meditrack-0.0.1-SNAPSHOT.jar"
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

if [ ! -f "$JAR_FILE" ]; then
    echo "Jar file $JAR_FILE does not exist! Build first."
    exit 1
fi

echo "Starting new instance on port ${PORT}..."
# Start detached from Jenkins
setsid java -jar "$JAR_FILE" --server.port=${PORT} > "$LOG_FILE" 2>&1 < /dev/null &
echo "Deployment auf Port ${PORT} abgeschlossen."
