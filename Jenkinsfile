pipeline {
    agent any

    environment {
        // nur einfache Konstanten!
        BASE_DYNAMIC_PORT = "9093"
    }

    stages {

        stage('Init vars') {
            steps {
                script {
                    // Branch-Namen bestimmen (bei einfachem Pipeline-Job oft null)
                    def branch = env.BRANCH_NAME ?: 'main'
                    env.BRANCH_NAME = branch

                    // branch-sicher machen
                    env.BRANCH_NAME_SAFE = branch.replaceAll('[^A-Za-z0-9_-]', '-')

                    // Servicename & Deploy-Pfad
                    env.SERVICE_NAME = "meditrack-${env.BRANCH_NAME_SAFE}"
                    env.DEPLOY_DIR = "/opt/${env.SERVICE_NAME}"

                    echo "Init:"
                    echo "  BRANCH_NAME      = ${env.BRANCH_NAME}"
                    echo "  BRANCH_NAME_SAFE = ${env.BRANCH_NAME_SAFE}"
                    echo "  SERVICE_NAME     = ${env.SERVICE_NAME}"
                    echo "  DEPLOY_DIR       = ${env.DEPLOY_DIR}"
                }
            }
        }

        stage('Assign Port') {
            steps {
                script {
                    // feste Ports für bestimmte Branches
                    def staticPorts = [
                        "main": 9090,
                        "test": 9091,
                        "features": 9092
                    ]

                    int port
                    if (staticPorts.containsKey(env.BRANCH_NAME)) {
                        port = staticPorts[env.BRANCH_NAME]
                    } else {
                        // dynamische Port-Berechnung
                        def hash = Math.abs(env.BRANCH_NAME.hashCode())
                        port = BASE_DYNAMIC_PORT.toInteger() + (hash % 50)
                    }

                    env.PORT = port.toString()
                    echo "Assigned PORT = ${env.PORT}"
                }
            }
        }

        stage('Load Jenkins.env') {
            steps {
                script {
                    def filePath = 'Jenkins.env'
                    if (!fileExists(filePath)) {
                        error "Jenkins.env nicht gefunden im Workspace. Bitte die Datei neben die Jenkinsfile legen."
                    }

                    def content = readFile(filePath)
                    content.split('\n').each { line ->
                        line = line.trim()
                        if (!line || line.startsWith('#')) return

                        def idx = line.indexOf('=')
                        if (idx <= 0) return

                        def key = line.substring(0, idx).trim()
                        def val = line.substring(idx + 1).trim()

                        // in die Jenkins-Env übernehmen
                        env[key] = val
                        echo "Env aus Datei gesetzt: ${key}=****"
                    }
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

        stage('Deploy JAR') {
            steps {
                script {
                    sh """
                      sudo mkdir -p "${DEPLOY_DIR}"
                      sudo cp target/*.jar "${DEPLOY_DIR}/app.jar"
                      sudo chmod +x "${DEPLOY_DIR}/app.jar"
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
ExecStart=/usr/bin/java -jar ${env.DEPLOY_DIR}/app.jar --server.port=${env.PORT}
Restart=always
RestartSec=10
Environment=SPRING_DATASOURCE_URL=${env.SPRING_DATASOURCE_URL}
Environment=SPRING_DATASOURCE_USERNAME=${env.SPRING_DATASOURCE_USERNAME}
Environment=SPRING_DATASOURCE_PASSWORD=${env.SPRING_DATASOURCE_PASSWORD}
Environment=SPRING_JPA_HIBERNATE_DDL_AUTO=${env.SPRING_JPA_HIBERNATE_DDL_AUTO}

[Install]
WantedBy=multi-user.target
"""

                    writeFile file: "service.tmp", text: serviceFile

                    sh """
                      sudo mv service.tmp /etc/systemd/system/${env.SERVICE_NAME}.service
                      sudo systemctl daemon-reload
                      sudo systemctl enable ${env.SERVICE_NAME}.service
                    """
                }
            }
        }

        stage('Restart service') {
            steps {
                sh """
                  sudo systemctl restart ${env.SERVICE_NAME}.service
                  sudo systemctl status ${env.SERVICE_NAME}.service --no-pager || true
                """
            }
        }
    }

    post {
        always {
            echo "🏁 Build finished for branch ${env.BRANCH_NAME ?: 'unbekannt'} on port ${env.PORT ?: 'unbekannt'}"
        }
    }
}
