pipeline {
    agent any

    environment {
        // nur einfache Konstanten hier!
        BASE_DYNAMIC_PORT = "9093"
    }

    stages {

        stage('Init / Branch & Service') {
            steps {
                script {
                    // Fallback, falls BRANCH_NAME null ist (kein Multibranch-Job)
                    def branchName = env.BRANCH_NAME ?: 'main'

                    // "sicherer" Branch-Name für Dateinamen / Service-Namen
                    env.BRANCH_NAME_SAFE = branchName.replaceAll('[^A-Za-z0-9_-]', '-')

                    // Service-Name und Deploy-Verzeichnis
                    env.SERVICE_NAME = "meditrack-${env.BRANCH_NAME_SAFE}"
                    env.DEPLOY_DIR   = "/opt/${env.SERVICE_NAME}"

                    // Port bestimmen
                    def staticPorts = [
                        "main"    : 9090,
                        "test"    : 9091,
                        "features": 9092
                    ]

                    int p
                    if (staticPorts.containsKey(branchName)) {
                        p = staticPorts[branchName]
                    } else {
                        int base = env.BASE_DYNAMIC_PORT.toInteger()
                        int hash = Math.abs(branchName.hashCode())
                        p = base + (hash % 50)
                    }

                    env.PORT = "${p}"
                    echo "Assigned PORT = ${env.PORT}"
                    echo "Service  : ${env.SERVICE_NAME}"
                    echo "DeployDir: ${env.DEPLOY_DIR}"
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
                    // JAR nach /opt/<service>/ legen
                    sh """
                        sudo mkdir -p ${env.DEPLOY_DIR}
                        sudo cp target/*.jar ${env.DEPLOY_DIR}/app.jar
                        sudo chmod +x ${env.DEPLOY_DIR}/app.jar
                    """
                }
            }
        }

        stage('Create systemd service') {
            steps {
                script {
                    // Database.env einlesen (liegt im selben Ordner wie Jenkinsfile)
                    def dbEnv = readProperties file: 'Database.env'

                    def serviceFile = """
[Unit]
Description=MediTrack Service for ${env.BRANCH_NAME_SAFE}
After=network.target

[Service]
User=root
ExecStart=/usr/bin/java -jar ${env.DEPLOY_DIR}/app.jar --server.port=${env.PORT}
Restart=always
RestartSec=10
Environment=SPRING_DATASOURCE_URL=${dbEnv.SPRING_DATASOURCE_URL}
Environment=SPRING_DATASOURCE_USERNAME=${dbEnv.SPRING_DATASOURCE_USERNAME}
Environment=SPRING_DATASOURCE_PASSWORD=${dbEnv.SPRING_DATASOURCE_PASSWORD}
Environment=SPRING_JPA_HIBERNATE_DDL_AUTO=${dbEnv.SPRING_JPA_HIBERNATE_DDL_AUTO}

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
            script {
                def branchName = env.BRANCH_NAME ?: 'main'
                def port       = env.PORT ?: 'unbekannt'
                echo "🏁 Build finished for branch ${branchName} on port ${port}"
            }
        }
    }
}
