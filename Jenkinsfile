pipeline {
    agent any

    environment {
        // Default port (fallback)
        APP_PORT = "9093"
    }

    stages {

        stage('Determine Port') {
            steps {
                script {
                    if (env.BRANCH_NAME == 'main') {
                        APP_PORT = "9090"
                    } else if (env.BRANCH_NAME == 'test') {
                        APP_PORT = "9091"
                    } else if (env.BRANCH_NAME.startsWith("feature/")) {
                        APP_PORT = "9092"
                    } else {
                        APP_PORT = "9093"
                    }

                    echo "Deploying branch '${env.BRANCH_NAME}' on port ${APP_PORT}"
                }
            }
        }

        stage('Kill old MediTrack instances') {
            steps {
                sh '''
                    echo "🔪 Killing MediTrack processes on ports 9090–9099..."
                    for port in {9090..9099}; do
                        pid=$(lsof -t -i:$port || true)
                        if [ ! -z "$pid" ]; then
                            echo "Killing PID $pid on port $port"
                            kill -9 $pid || true
                        fi
                    done

                    echo "🕑 Waiting 5 seconds before continuing..."
                    sleep 5
                '''
            }
        }

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh '''
                    echo "🔨 Building JAR..."
                    ./mvnw clean package -DskipTests
                '''
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    echo "🚀 Starting MediTrack on port ${APP_PORT}"

                    nohup java -jar target/*.jar \
                        --server.port=${APP_PORT} \
                        > meditrack_${APP_PORT}.log 2>&1 &
                '''
            }
        }

        stage('Show Running') {
            steps {
                sh '''
                    echo "🔍 Active MediTrack processes:"
                    ps aux | grep -i meditrack | grep -v grep || true
                '''
            }
        }
    }
}
