pipeline {
    agent any

    environment {
        DEPLOY_DIR = isUnix() ? "/opt/meditrack/${env.BRANCH_NAME}" : "C:\\MediTrack\\${env.BRANCH_NAME}"
        SERVER_PORT = env.BRANCH_NAME == 'main' ? 9090 : 9090 + (env.BRANCH_NAME.hashCode().abs() % 100)
        MAVEN_HOME = isUnix() ? "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11" : "C:\\Maven"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Clean Workspace') {
            steps {
                deleteDir()
            }
        }

        stage('Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh "${MAVEN_HOME}/bin/mvn clean package -DskipTests"
                    } else {
                        bat "${MAVEN_HOME}\\bin\\mvn clean package -DskipTests"
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    if (isUnix()) {
                        sh "${MAVEN_HOME}/bin/mvn test"
                    } else {
                        bat "${MAVEN_HOME}\\bin\\mvn test"
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    if (isUnix()) {
                        sh """
                        sudo systemctl stop meditrack@${env.BRANCH_NAME}.service || true
                        sudo mkdir -p ${DEPLOY_DIR}
                        sudo cp target/meditrack-0.0.1-SNAPSHOT.jar ${DEPLOY_DIR}/
                        sudo systemctl daemon-reload
                        sudo systemctl set-environment SERVER_PORT=${SERVER_PORT}
                        sudo systemctl enable meditrack@${env.BRANCH_NAME}.service
                        sudo systemctl start meditrack@${env.BRANCH_NAME}.service
                        """
                    } else {
                        // Windows Deployment: kopiere JAR, starte als Windows Service
                        bat """
                        if exist ${DEPLOY_DIR} rmdir /S /Q ${DEPLOY_DIR}
                        mkdir ${DEPLOY_DIR}
                        copy target\\meditrack-0.0.1-SNAPSHOT.jar ${DEPLOY_DIR}\\
                        REM Hier kann ein Windows Service-Skript oder NSSM verwendet werden
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ Branch ${env.BRANCH_NAME} erfolgreich deployed auf Port ${SERVER_PORT}"
        }
        failure {
            echo "❌ Deployment fehlgeschlagen für Branch ${env.BRANCH_NAME}"
        }
    }
}
