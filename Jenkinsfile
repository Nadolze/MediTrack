pipeline {
    agent any

    environment {
        MAVEN_HOME = 'C:\\Program Files\\Apache\\maven-3.9.3' // falls Maven installiert
    }

    stages {
        stage('Checkout') {
            steps {
                // holt den Code vom GitHub-Repo
                git branch: 'main', url: 'https://github.com/Nadolze/MediTrack.git'
            }
        }

        stage('Build') {
            steps {
                // baut das Projekt
                bat 'mvn clean package'
            }
        }

        stage('Test') {
            steps {
                // führt die Tests aus
                bat 'mvn test'
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
