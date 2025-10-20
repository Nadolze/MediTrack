pipeline {
    agent any

    environment {
        DEPLOY_DIR = "/opt/meditrack"
        JAR_NAME = "mediweb-0.0.1-SNAPSHOT.jar"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Nadolze/MediTrack.git'
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
                // Stoppe bestehenden Dienst (falls vorhanden)
                sh "systemctl stop meditrack || true"

                // Kopiere neues JAR
                sh "cp target/${JAR_NAME} ${DEPLOY_DIR}/"

                // Starte Service neu
                sh "systemctl start meditrack"
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
