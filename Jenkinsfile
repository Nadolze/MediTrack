pipeline {
    agent any
    environment {
        PORT = ''
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
                    // Branch-abhängige Port-Zuweisung
                    if (env.BRANCH_NAME == 'main') {
                        PORT = '9090'
                    } else if (env.BRANCH_NAME == 'test') {
                        PORT = '9091'
                    } else if (env.BRANCH_NAME == 'features') {
                        PORT = '9092'
                    } else {
                        PORT = '9093'
                    }
                    echo "👉 Branch '${env.BRANCH_NAME}' wird auf Port ${PORT} laufen."
                }
            }
        }

        stage('Build') {
            steps {
                sh """
                    echo "Using system Maven and Java..."
                    mvn -v
                    java -version
                    mvn clean package -DskipTests
                """
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
                        # Spring Boot direkt starten und Logs in Jenkins ausgeben
                        java -jar target/meditrack-0.0.1-SNAPSHOT.jar --server.port=${PORT} | tee app_${PORT}.log &
                    """
                    echo "Deployment auf Port ${PORT} abgeschlossen."
                }
            }
        }
    }

    post {
        always {
            echo "Branch '${env.BRANCH_NAME}' is deployed on port ${PORT}"
        }
    }
}
