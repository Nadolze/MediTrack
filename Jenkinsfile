pipeline {
    agent any
    environment {
        APP_DIR = "/opt/meditrack/main"
        JAR_NAME = "meditrack-0.0.1-SNAPSHOT.jar"
        SERVICE_NAME = "meditrack"
    }
    stages {
        stage('Stop old service') {
            steps {
                echo "🔧 Stopping old Meditrack service (if running)..."
                sh "sudo systemctl stop ${SERVICE_NAME} || true"
            }
        }

        stage('Clean Workspace') {
            steps {
                echo "🧹 Cleaning workspace..."
                deleteDir()
            }
        }

        stage('Checkout') {
            steps {
                echo "📦 Checkout code from Git..."
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo "🔧 Building JAR with Maven..."
                sh "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11/bin/mvn clean package -DskipTests"
                sh "test -f target/${JAR_NAME}"
            }
        }

        stage('Deploy') {
            steps {
                echo "🚀 Deploying JAR to ${APP_DIR}..."
                sh "sudo rm -f ${APP_DIR}/${JAR_NAME}"
                sh "sudo cp target/${JAR_NAME} ${APP_DIR}/"
            }
        }

        stage('Restart service') {
            steps {
                echo "🔁 Restarting systemd service..."
                sh "sudo systemctl daemon-reload"
                sh "sudo systemctl enable ${SERVICE_NAME}"
                sh "sudo systemctl restart ${SERVICE_NAME}"
                sh "sudo systemctl status ${SERVICE_NAME} --no-pager"
            }
        }
    }

    post {
        success {
            echo "✅ Deployment completed successfully!"
        }
        failure {
            echo "❌ Deployment failed!"
        }
    }
}
