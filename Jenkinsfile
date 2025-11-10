pipeline {
    agent any

    environment {
        MAVEN_HOME = isUnix() ? "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11" : "C:\\Maven"
    }

    stages {

        stage('Clean Workspace') {
            steps {
                deleteDir() // vor Checkout!
            }
        }

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Setup') {
            steps {
                script {
                    // Branch und Deploy-Verzeichnis bestimmen
                    def branchName = env.BRANCH_NAME
                    DEPLOY_DIR = isUnix() ? "/opt/meditrack/${branchName}" : "C:\\MediTrack\\${branchName}"

                    // Port: main = 9090, alle anderen auf nächstem freien Port
                    SERVER_PORT = branchName == 'main' ? 9090 : findFreePort()

                    echo "Branch: ${branchName}"
                    echo "Deploy dir: ${DEPLOY_DIR}"
                    echo "Server Port: ${SERVER_PORT}"
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh "${MAVEN_HOME}/bin/mvn clean package -DskipTests"
                    } else {
                        bat "\"${MAVEN_HOME}\\bin\\mvn\" clean package -DskipTests"
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    if (isUnix()) {
                        sh """
                        mkdir -p ${DEPLOY_DIR}
                        cp target/meditrack-*.jar ${DEPLOY_DIR}/meditrack.jar
                        # systemd Service erstellen oder aktualisieren
                        cat <<EOF | sudo tee /etc/systemd/system/meditrack-${BRANCH_NAME}.service
                        [Unit]
                        Description=MediTrack ${BRANCH_NAME} Service
                        After=network.target

                        [Service]
                        User=jenkins
                        WorkingDirectory=${DEPLOY_DIR}
                        ExecStart=/usr/bin/java -jar ${DEPLOY_DIR}/meditrack.jar --server.port=${SERVER_PORT}
                        Restart=always
                        RestartSec=10

                        [Install]
                        WantedBy=multi-user.target
                        EOF
                        sudo systemctl daemon-reload
                        sudo systemctl enable meditrack-${BRANCH_NAME}.service
                        sudo systemctl restart meditrack-${BRANCH_NAME}.service
                        """
                    } else {
                        // Windows Deployment mit NSSM oder PowerShell Service hier
                    }
                }
            }
        }
    }
}
