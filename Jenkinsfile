pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/Nadolze/MediTrack.git'
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
    }

    post {
        success {
            echo '✅ Build und Tests erfolgreich!'
        }
        failure {
            echo '❌ Fehler beim Build oder Test.'
        }
    }
}
