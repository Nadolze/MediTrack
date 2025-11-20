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
                    // Branch-Port Mapping
                    if (env.BRANCH_NAME == 'main') {
                        PORT = 9090
                    } else if (env.BRANCH_NAME == 'test') {
                        PORT = 9091
                    } else if (env.BRANCH_NAME.startsWith('features')) {
                        PORT = 9092
                    } else {
                        PORT = 9093
                    }
                    echo "👉 Branch '${env.BRANCH_NAME}' wird auf Port ${PORT} laufen."
                }
            }
        }

        stage('Build') {
            steps {
                withEnv(["PATH+MAVEN=${MAVEN_HOME}/bin"]) {
                    sh '''
                        echo "Using Maven Version:"
                        mvn -v
                        mvn clean package -DskipTests
                    '''
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
                        # Start Java-Prozess unabhängig von Jenkins
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
            script {
                echo "Branch ${env.BRANCH_NAME} läuft auf Port:"
                echo "${PORT}"
            }
        }
    }
}
