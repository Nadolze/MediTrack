pipeline {
    agent any
    environment {
        // Maven-Version explizit setzen
        MAVEN_HOME = tool name: 'Maven_3.9.11', type: 'maven'
        PATH = "${MAVEN_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Determine Port') {
            steps {
                script {
                    // Branch-Port Mapping
                    switch(env.BRANCH_NAME) {
                        case 'main':
                            PORT = 9090
                            break
                        case 'test':
                            PORT = 9091
                            break
                        case ~/features?.*/:
                            PORT = 9092
                            break
                        default:
                            PORT = 9093
                    }
                    echo "👉 Branch '${env.BRANCH_NAME}' wird auf Port ${PORT} laufen."
                }
            }
        }

        stage('Build') {
            steps {
                echo "Using Maven Version:"
                sh 'mvn -v'
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Deploy') {
            steps {
                script {
                    echo "Deploying branch '${env.BRANCH_NAME}' on port ${PORT}"

                    // Alte Instanz stoppen mit Timeout 5s
                    sh """
                        echo Stopping old instance on port ${PORT} (timeout 5s)...
                        PID=\$(lsof -t -i:${PORT} || true)
                        if [ -n "\$PID" ]; then
                            kill \$PID
                            sleep 5
                            if kill -0 \$PID 2>/dev/null; then
                                kill -9 \$PID
                            fi
                        else
                            echo "No process running on port ${PORT}"
                        fi
                    """

                    echo "Starting new instance on port ${PORT}"

                    // Java-Prozess im Workspace starten
                    dir("${env.WORKSPACE}") {
                        sh """
                            nohup java -jar target/meditrack-0.0.1-SNAPSHOT.jar \
                                --server.port=${PORT} \
                                > app_${PORT}.log 2>&1 &
                        """
                    }

                    echo "Deployment auf Port ${PORT} abgeschlossen."
                }
            }
        }
    }

    post {
        always {
            script {
                echo "Branch ${env.BRANCH_NAME} läuft auf Port:"
                echo "${PORT}"
            }
        }
    }
}
