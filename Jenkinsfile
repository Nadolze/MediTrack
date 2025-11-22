pipeline {
    agent any

    environment {
        // Basis-Port für dynamische Branches
        BASE_DYNAMIC_PORT = "9093"
        BRANCH_NAME_SAFE = "${env.BRANCH_NAME.replaceAll('[^A-Za-z0-9_-]', '-')}"
        SERVICE_NAME      = "meditrack-${BRANCH_NAME_SAFE}"
        DEPLOY_DIR        = "/opt/${SERVICE_NAME}"

        // Name der Env-Datei im Workspace
        LOCAL_ENV_FILE    = "Database.env"
        // Zielpfad auf dem Server
        REMOTE_ENV_FILE   = "/etc/meditrack/${SERVICE_NAME}.env"
    }

    stages {

        stage('Assign Port') {
            steps {
                script {
                    // Statische Branch-Ports
                    def staticPorts = [
                        "main"    : 9090,
                        "test"    : 9091,
                        "features": 9092
                    ]

                    if (staticPorts.containsKey(env.BRANCH_NAME)) {
                        PORT = staticPorts[env.BRANCH_NAME]
                    } else {
                        // Dynamische Port-Berechnung
                        def hash = Math.abs(env.BRANCH_NAME.hashCode())
                        PORT = BASE_DYNAMIC_PORT.toInteger() + (hash % 50)
                    }

                    echo "Assigned PORT = ${PORT}"
                }
            }
        }

        stage('Build Maven') {
            steps {
                sh """
                    mvn -B -DskipTests clean package
                """
            }
        }

        stage('Deploy') {
            steps {
                script {
                    sh """
                        set -e

                        echo "🔧 Erstelle Deploy-Verzeichnis ${DEPLOY_DIR}..."
                        sudo mkdir -p ${DEPLOY_DIR}

                        echo "📦 Kopiere JAR nach ${DEPLOY_DIR}/app.jar..."
                        sudo cp target/*.jar ${DEPLOY_DIR}/app.jar
                        sudo chmod +x ${DEPLOY_DIR}/app.jar

                        echo "📄 Bereite Env-Datei vor..."

                        # /etc/meditrack anlegen
                        sudo mkdir -p /etc/meditrack

                        if [ -f "${LOCAL_ENV_FILE}" ]; then
                            echo "➡️  Kopiere ${LOCAL_ENV_FILE} nach ${REMOTE_ENV_FILE}..."
                            sudo cp "${LOCAL_ENV_FILE}" "${REMOTE_ENV_FILE}"
                            sudo chmod 600 "${REMOTE_ENV_FILE}" || true
                        else
                            echo "⚠️ WARNUNG: Env-Datei ${LOCAL_ENV_FILE} nicht gefunden!"
                            echo "⚠️ Der Service startet ohne DB-Konfiguration."
                        fi
                    """
                }
            }
        }

        stage('Create systemd service') {
            steps {
                script {
                    def serviceFile = """
[Unit]
Description=MediTrack Service for ${env.BRANCH_NAME}
After=network.target

[Service]
User=root
EnvironmentFile=${REMOTE_ENV_FILE}
ExecStart=/usr/bin/java -jar ${DEPLOY_DIR}/app.jar --server.port=${PORT}
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
"""

                    writeFile file: "service.tmp", text: serviceFile

                    sh """
                        echo "📝 Installiere systemd-Service ${SERVICE_NAME}.service ..."
                        sudo mv service.tmp /etc/systemd/system/${SERVICE_NAME}.service
                        sudo systemctl daemon-reload
                        sudo systemctl enable ${SERVICE_NAME}.service
                    """
                }
            }
        }

        stage('Restart service') {
            steps {
                sh """
                    echo "🔁 Starte Service ${SERVICE_NAME}.service neu ..."
                    sudo systemctl restart ${SERVICE_NAME}.service
                    sudo systemctl status ${SERVICE_NAME}.service --no-pager || true
                """
            }
        }
    }

    post {
        always {
            script {
                def portInfo = (binding.hasVariable('PORT') && PORT != null) ? PORT : "unbekannt"
                echo "🏁 Build finished for branch ${env.BRANCH_NAME} on port ${portInfo}"
            }
        }
    }
}
