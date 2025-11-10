pipeline {
    agent any
    environment {
        BRANCH_DEPLOY_DIR = isUnix() ? "/opt/meditrack/${env.BRANCH_NAME}" : "C:\\MediTrack\\${env.BRANCH_NAME}"
        PORT_OFFSET = BRANCH_PORT_OFFSET[env.BRANCH_NAME] ?: 2
        SERVER_PORT = 9090 + PORT_OFFSET
    }
    stages {
        stage('Clean & Stop Old Instances') {
            steps {
                script {
                    if (isUnix()) {
                        sh """
                            echo "🔧 Stoppe alte Instanzen..."
                            pkill -f "${BRANCH_DEPLOY_DIR}/meditrack-*.jar" || true
                            rm -rf ${BRANCH_DEPLOY_DIR}/*
                            mkdir -p ${BRANCH_DEPLOY_DIR}
                        """
                    } else {
                        bat """
                            echo Stoppe alte Instanzen...
                            taskkill /F /IM meditrack-*.jar || echo Kein Prozess
                            if exist "${BRANCH_DEPLOY_DIR}" rmdir /S /Q "${BRANCH_DEPLOY_DIR}"
                            mkdir "${BRANCH_DEPLOY_DIR}"
                        """
                    }
                }
            }
        }

        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh "mvn clean package -DskipTests"
                        sh "cp target/meditrack-*.jar ${BRANCH_DEPLOY_DIR}/"
                    } else {
                        bat "mvn clean package -DskipTests"
                        bat "copy target\\meditrack-*.jar ${BRANCH_DEPLOY_DIR}\\"
                    }
                }
            }
        }

        stage('Deploy Service') {
            steps {
                script {
                    if (isUnix()) {
                        sh """
                            JAR_FILE=\$(ls ${BRANCH_DEPLOY_DIR}/meditrack-*.jar)
                            SERVICE_FILE="/etc/systemd/system/meditrack-${env.BRANCH_NAME}.service"

                            # Alte Service-Datei löschen
                            sudo systemctl stop meditrack-${env.BRANCH_NAME} || true
                            sudo systemctl disable meditrack-${env.BRANCH_NAME} || true
                            sudo rm -f \${SERVICE_FILE}

                            # Neue Service-Datei erstellen
                            cat <<EOF | sudo tee \${SERVICE_FILE}
[Unit]
Description=MediTrack Spring Boot - Branch ${env.BRANCH_NAME}
After=network.target

[Service]
User=jenkins
WorkingDirectory=${BRANCH_DEPLOY_DIR}
ExecStart=/usr/bin/java -jar \${JAR_FILE} --server.port=${SERVER_PORT}
SuccessExitStatus=143
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF

                            sudo systemctl daemon-reload
                            sudo systemctl enable meditrack-${env.BRANCH_NAME}.service
                            sudo systemctl restart meditrack-${env.BRANCH_NAME}.service
                        """
                    } else {
                        bat """
                            set JAR_FILE=${BRANCH_DEPLOY_DIR}\\meditrack-*.jar
                            nssm stop MediTrack-${env.BRANCH_NAME} || echo Kein laufender Service
                            nssm remove MediTrack-${env.BRANCH_NAME} confirm
                            nssm install MediTrack-${env.BRANCH_NAME} "java" "-jar %JAR_FILE% --server.port=${SERVER_PORT}"
                            nssm start MediTrack-${env.BRANCH_NAME}
                        """
                    }
                }
            }
        }
    }
}
