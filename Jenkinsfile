pipeline {
    agent any

    environment {
        MAVEN_HOME = tool name: 'Maven_3.9.11', type: 'maven'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Determine Port') {
            steps {
                script {
                    def basePort = 9090
                    if (env.BRANCH_NAME == 'main') {
                        env.PORT = "${basePort}"
                    } else if (env.BRANCH_NAME == 'test') {
                        env.PORT = "${basePort + 1}"
                    } else {
                        // Feature-Branches ab 9092
                        env.PORT = "${basePort + 2 + Math.abs(env.BRANCH_NAME.hashCode() % 100)}"
                    }
                    echo "👉 Branch '${env.BRANCH_NAME}' wird auf Port ${env.PORT} laufen."
                }
            }
        }

        stage('Build') {
            steps {
                withEnv(["PATH+MAVEN=${MAVEN_HOME}/bin"]) {
                    sh """
                        echo "Using Maven Version:"
                        mvn -v
                        mvn clean package -DskipTests
                    """
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    sh """
                        echo "Stopping old instance on port ${PORT} (timeout 5s)..."
                        PID=\$(lsof -t -i:${PORT} || true)
                        if [ -n "\$PID" ]; then
                            kill \$PID
                            sleep 5
                            if kill -0 \$PID 2>/dev/null; then
                                echo "Force killing PID \$PID"
                                kill -9 \$PID
                            fi
                        else
                            echo "No process running on port ${PORT}"
                        fi

                        echo "Starting new instance on port ${PORT}..."
                        setsid java -jar target/meditrack-0.0.1-SNAPSHOT.jar --server.port=${PORT} > app_${PORT}.log 2>&1 < /dev/null &
                    """
                    echo "Deployment auf Port ${PORT} abgeschlossen."
                    echo "Branch ${env.BRANCH_NAME} läuft nun auf Port ${PORT}"
                }
            }
        }
    }

    post {
        success {
            echo "✅ Branch ${env.BRANCH_NAME} erfolgreich deployed auf Port ${env.PORT}"
        }
        failure {
            echo "❌ Deployment für Branch ${env.BRANCH_NAME} fehlgeschlagen"
        }
    }
}
