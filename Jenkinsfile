pipeline {
    agent any

    environment {
        DEPLOY_DIR = "/opt/meditrack"
        JAR_NAME = "mediweb-0.0.1-SNAPSHOT.jar"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Nadolze/MediTrack.git',
                    credentialsId: 'github-creds'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Deploy') {
            steps {
                // systemctl stop/start benötigt sudo ohne Passwort
                sh "sudo systemctl stop meditrack || true"
                sh "cp target/${JAR_NAME} ${DEPLOY_DIR}/"
                sh "sudo systemctl start meditrack"
            }
        }
    }

    post {
        success {
            echo "✅ Build, Test und Deployment erfolgreich!"
        }
        failure {
            echo "❌ Fehler im Build/Test/Deployment!"
        }
    }
}
