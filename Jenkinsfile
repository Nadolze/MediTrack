pipeline {
    agent any

    tools {
        maven "Maven_3.9.11"
    }

    stages {

        stage('Build') {
            steps {
                sh """
                    echo "Using Maven Version:"
                    mvn -v
                    mvn clean package -DskipTests
                """
            }
        }

        stage('Deploy') {
            steps {
                script {
                    def port

                    if (env.BRANCH_NAME == "main") {
                        port = 9090
                    } else if (env.BRANCH_NAME == "test") {
                        port = 9091
                    } else if (env.BRANCH_NAME.startsWith("feature/")) {
                        port = 9092
                    } else {
                        port = 9093
                    }

                    echo "Deploying branch '${env.BRANCH_NAME}' on port ${port}"

                    sh """
                        echo "Stopping old instance on port ${port} (timeout 5s)..."
                        PID=\$(lsof -t -i:${port} || true)

                        if [ -n "\$PID" ]; then
                            kill \$PID || true

                            # warten bis Prozess weg ist (max. 5 Sekunden)
                            for i in {1..5}; do
                                if lsof -t -i:${port} > /dev/null; then
                                    echo "Waiting for process to stop..."
                                    sleep 1
                                else
                                    echo "Process on port ${port} terminated."
                                    break
                                fi
                            done

                            # Falls nach 5 Sekunden der Prozess noch lebt -> kill -9
                            if lsof -t -i:${port} > /dev/null; then
                                echo "Force killing process on port ${port}"
                                kill -9 \$PID || true
                            fi
                        else
                            echo "No process running on port ${port}"
                        fi
                    """

                    sh """
                        echo "Starting new instance on port ${port}"
                        nohup java -jar target/*.jar --server.port=${port} > app_${port}.log 2>&1 &
                    """

                    echo "Deployment auf Port ${port} abgeschlossen."
                }
            }
        }
    }

    post {
        always {
            echo "Branch ${env.BRANCH_NAME} läuft auf Port:"
            script {
                if (env.BRANCH_NAME == "main") {
                    echo "9090"
                } else if (env.BRANCH_NAME == "test") {
                    echo "9091"
                } else if (env.BRANCH_NAME.startsWith("feature/")) {
                    echo "9092"
                } else {
                    echo "9093"
                }
            }
        }
    }
}
