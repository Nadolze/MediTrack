pipeline {
    agent any

    stages {
        stage('Setup') {
            steps {
                script {
                    // --- Dynamische Variablen ---
                    DEPLOY_DIR = isUnix() ? "/opt/meditrack/${env.BRANCH_NAME}" : "C:\\MediTrack\\${env.BRANCH_NAME}"
                    SERVER_PORT = env.BRANCH_NAME == 'main' ? 9090 : 9090 + (env.BRANCH_NAME.hashCode().abs() % 100)
                    MAVEN_HOME = isUnix() ? "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11" : "C:\\Maven"

                    echo "Branch: ${env.BRANCH_NAME}"
                    echo "Deploy dir: ${DEPLOY_DIR}"
                    echo "Server Port: ${SERVER_PORT}"
                    echo "Maven Home: ${MAVEN_HOME}"
                }
            }
        }

        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Clean Workspace') {
            steps { deleteDir() }
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
                        # --- Alte Instanz stoppen ---
                        sudo systemctl stop meditrack@${env.BRANCH_NAME}.service || true

                        # --- Deploy-Verzeichnis anlegen ---
                        sudo mkdir -p ${DEPLOY_DIR}
                        sudo cp target/meditrack-0.0.1-SNAPSHOT.jar ${DEPLOY_DIR}/

                        # --- Systemd-Service erstellen ---
                        sudo bash -c 'cat > /etc/systemd/system/meditrack@.service <<EOF
                        [Unit]
                        Description=MediTrack Spring Boot Application (%i)
                        After=network.target

                        [Service]
                        User=jenkins
                        WorkingDirectory=${DEPLOY_DIR}
                        ExecStart=/usr/bin/java -jar ${DEPLOY_DIR}/meditrack-0.0.1-SNAPSHOT.jar --server.port=\${SERVER_PORT}
                        SuccessExitStatus=143
                        Restart=always
                        Environment=SERVER_PORT=${SERVER_PORT}

                        [Install]
                        WantedBy=multi-user.target
                        EOF'

                        sudo systemctl daemon-reload
                        sudo systemctl enable meditrack@${env.BRANCH_NAME}.service
                        sudo systemctl start meditrack@${env.BRANCH_NAME}.service
                        """
                    } else {
                        bat """
                        REM --- Windows Deploy ---
                        if exist ${DEPLOY_DIR} rmdir /S /Q ${DEPLOY_DIR}
                        mkdir ${DEPLOY_DIR}
                        copy target\\meditrack-0.0.1-SNAPSHOT.jar ${DEPLOY_DIR}\\

                        REM --- Windows Service hier starten (z.B. NSSM) ---
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ Branch ${env.BRANCH_NAME} deployed auf Port ${SERVER_PORT}"
        }
        failure {
            echo "❌ Deployment fehlgeschlagen für Branch ${env.BRANCH_NAME}"
        }
    }
}
