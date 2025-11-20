pipeline {
    agent any
    environment {
        PORT = "${BRANCH_NAME == 'main' ? '9090' : '9091'}"
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo "Using Maven Version:"
                sh "mvn -v"
                sh "mvn clean package -DskipTests"
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Ensure deploy.sh is executable
                    sh "chmod +x deploy.sh"

                    // Deploy and show first 100 lines of log
                    sh """
                        ./deploy.sh ${PORT}
                        echo "==== First 100 lines of app_${PORT}.log ===="
                        head -n 100 app_${PORT}.log
                    """
                }
            }
        }
    }
    post {
        always {
            echo "Branch ${BRANCH_NAME} is deployed on port ${PORT}"
        }
    }
}
