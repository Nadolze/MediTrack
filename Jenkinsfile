pipeline {
    agent any

    environment {
        // Main branch bekommt festen Port
        SERVER_PORT = ''
        DEPLOY_DIR = ''
        MAVEN_HOME = ''
    }

    stages {
        stage('Clean Workspace') {
            steps {
                deleteDir()
            }
        }

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Setup Environment') {
            steps {
                script {
                    // Branch-spezifischer Deploy-Ordner
                    def branchName = env.BRANCH_NAME
                    if (isUnix()) {
                        DEPLOY_DIR = "/opt/meditrack/${branchName}"
                        MAVEN_HOME = "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11"
                    } else {
                        DEPLOY_DIR = "C:\\MediTrack\\${branchName}"
                        MAVEN_HOME = "C:\\Maven"
                    }

                    // Port-Zuordnung
                    SERVER_PORT = branchName == 'main' ? 9090 : (9090 + Math.abs(branchName.hashCode() % 100))

                    echo "Branch: ${branchName}"
                    echo "Deploy dir: ${DEPLOY_DIR}"
                    echo "Server Port: ${SERVER_PORT}"
                    echo "Maven Home: ${MAVEN_HOME}"
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh "${MAVEN_HOME}/bin/mvn clean package -DskipTests"
                    } else {
                        bat "${MAVEN_HOME}\\bin\\mvn.cmd clean package -DskipTests"
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    def jarName = sh(returnStdout: true, script: "ls target/*.jar | head -n1").trim()
                    if (jarName == '') {
                        error "Keine JAR-Datei gefunden!"
                    }

                    // Deploy-Ordner erstellen
                    sh "mkdir -p ${DEPLOY_DIR}"
                    sh "cp ${jarName} ${DEPLOY_DIR}/meditrack-${env.BRANCH_NAME}.jar"

                    // Systemd-Service für Branch anlegen (nur Linux)
                    if (isUnix()) {
                        def serviceFile = "/etc/systemd/system/meditrack-${env.BRANCH_NAME}.service"
                        sh """
                        cat <<EOF | sudo tee ${serviceFile}
                        [Unit]
                        Description=MediTrack Spring Boot Application (${env.BRANCH_NAME})
                        After=network.target

                        [Service]
                        User=jenkins
                        ExecStart=/usr/bin/java -jar ${DEPLOY_DIR}/meditrack-${env.BRANCH_NAME}.jar --server.port=${SERVER_PORT}
                        SuccessExitStatus=143
                        Restart=always
                        RestartSec=10

                        [Install]
                        WantedBy=multi-user.target
                        EOF
                        sudo systemctl daemon-reload
                        sudo systemctl enable meditrack-${env.BRANCH_NAME}.service
                        sudo systemctl restart meditrack-${env.BRANCH_NAME}.service
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ Deployment erfolgreich für Branch ${env.BRANCH_NAME} auf Port ${SERVER_PORT}"
        }
        failure {
            echo "❌ Deployment fehlgeschlagen für Branch ${env.BRANCH_NAME}"
        }
    }
}
