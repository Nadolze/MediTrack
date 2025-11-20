pipeline {
    agent any

    environment {
        // Port für jeden Branch dynamisch
        PORT = "${env.BRANCH_NAME == 'test' ? '9091' : '9090'}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                script {
                    // Maven Tool aus Jenkins verwenden
                    def mvnHome = tool name: 'Maven_3.9.11', type: 'maven'
                    sh "${mvnHome}/bin/mvn -v"
                    sh "${mvnHome}/bin/mvn clean package -DskipTests"
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Deploy.sh relativ zum Workspace aufrufen
                    sh """
                    chmod +x ${env.WORKSPACE}/deploy.sh
                    echo "Deploying branch '${env.BRANCH_NAME}' on port ${PORT}..."
                    ${env.WORKSPACE}/deploy.sh ${PORT}
                    """
                }
            }
        }
    }

    post {
        always {
            echo "Branch '${env.BRANCH_NAME}' ist deployed auf Port ${PORT}"
        }
    }
}
