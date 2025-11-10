pipeline {
    agent any

    environment {
        DEPLOY_DIR = "/opt/meditrack/main"
        JAR_NAME = "meditrack-0.0.1-SNAPSHOT.jar"
        MAVEN_HOME = "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11"
    }

    stages {
        stage('Clean Old Processes & Files') {
            steps {
                echo "🔧 Stoppe alte Meditrack-Instanzen und lösche Deploy-Verzeichnis..."
                sh """
                sudo pkill -f ${JAR_NAME} || true
                sudo rm -rf ${DEPLOY_DIR}/*
                """
            }
        }

        stage('Checkout Code') {
            steps {
                echo "📦 Checkout aus Git..."
                deleteDir() // Workspace komplett löschen
                git branch: 'main',
                    url: 'https://github.com/Nadolze/MediTrack.git',
                    credentialsId: 'github-creds'
            }
        }

        stage('Build with Maven') {
            steps {
                echo "🔧 Starte Maven Build..."
                sh "${MAVEN_HOME}/bin/mvn clean package -DskipTests"
            }
        }

        stage('Deploy') {
            steps {
                echo "🚀 Deployment auf Server..."
                sh """
                sudo rm -f ${DEPLOY_DIR}/*.jar
                sudo cp target/${JAR_NAME} ${DEPLOY_DIR}/
                sudo nohup java -jar ${DEPLOY_DIR}/${JAR_NAME} --server.port=9090 > ${DEPLOY_DIR}/app.log 2>&1 &
                """
            }
        }
    }

    post {
        success {
            echo "✅ Build und Deployment erfolgreich. Meditrack läuft auf Port 9090."
        }
        failure {
            echo "❌ Build oder Deployment fehlgeschlagen. Log prüfen."
        }
    }
}
