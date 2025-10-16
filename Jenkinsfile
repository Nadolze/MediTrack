pipeline {
    agent any

    environment {
        MAVEN_HOME = 'C:\\Program Files\\Apache\\maven-3.9.11'
        PATH = "${env.MAVEN_HOME}\\bin;${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                echo '🔄 Hole Quellcode von GitHub...'
                git branch: 'main', url: 'https://github.com/Nadolze/MediTrack.git'
            }
        }

        stage('Build') {
            steps {
                echo '🏗️ Baue das Projekt...'
                bat 'mvn -B clean package'
            }
        }

        stage('Test') {
            steps {
                echo '🧪 Führe Unit-Tests aus...'
                bat 'mvn test'
            }
        }

        stage('Ergebnis') {
            steps {
                echo '📦 Build abgeschlossen — prüfe Testergebnisse!'
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
