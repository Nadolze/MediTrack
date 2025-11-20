pipeline {
    agent any

    environment {
        // Branch-Port-Zuordnung
        PORT = "${env.BRANCH_NAME == 'main' ? '9090' : (env.BRANCH_NAME == 'test' ? '9091' : '9092')}"
        MAVEN_HOME = tool name: 'Maven_3.9.11', type: 'maven'
        PATH = "${MAVEN_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

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
                    // sicherstellen, dass deploy.sh ausführbar ist
                    sh "chmod +x ${WORKSPACE}/deploy.sh"
                    // deploy.sh ausführen mit branch-spezifischem PORT
                    sh "${WORKSPACE}/deploy.sh ${PORT}"
                }
            }
        }
    }

    post {
        success {
            echo "Branch ${env.BRANCH_NAME} läuft auf Port ${PORT}"
        }
        failure {
            echo "Build oder Deployment fehlgeschlagen!"
        }
    }
}
