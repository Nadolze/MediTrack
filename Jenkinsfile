pipeline {
    agent any

    environment {
        // Basis-Port für dynamische Branches
        BASE_DYNAMIC_PORT = "9093"
        BRANCH_NAME_SAFE = "${env.BRANCH_NAME.replaceAll('[^A-Za-z0-9_-]', '-')}"
        SERVICE_NAME = "meditrack-${BRANCH_NAME_SAFE}"
        DEPLOY_DIR = "/opt/${SERVICE_NAME}"
        GIT_COMMIT_MESSAGE = sh(
                script: "git log -1 --pretty=%B",
                returnStdout: true
            ).trim()
    }

    stages {

        stage('Assign Port') {
            steps {
                script {
                    // Statische Branch-Ports
                    def staticPorts = [
                        "main": 9090,
                        "test": 9091,
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
        stage('Commit Info') {
            steps {
                script {
                    def msg = sh(script: "git log -1 --pretty=%B", returnStdout: true).trim()
                    def author = sh(script: "git log -1 --pretty='%an <%ae>'", returnStdout: true).trim()

                    echo "Last commit author: ${author}"
                    echo "Commit message: ${msg}"
                }
            }
        }

        stage('Build Maven') {
            steps {
                sh """
                    mvn -B clean verify
                """
            }
        }

        stage('Deploy') {
            steps {
                script {
                    sh """
                        sudo mkdir -p ${DEPLOY_DIR}
                        sudo cp target/*.jar ${DEPLOY_DIR}/app.jar
                        sudo chmod +x ${DEPLOY_DIR}/app.jar
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
ExecStart=/usr/bin/java -jar ${DEPLOY_DIR}/app.jar --server.port=${PORT}
Restart=always
RestartSec=10
EnvironmentFile=/opt/meditrack/envs/__BRANCH__.env
Environment=SPRING_DATASOURCE_URL=jdbc:mysql://82.165.255.70:3306/meditrack?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
Environment=SPRING_DATASOURCE_USERNAME=web_user
Environment=SPRING_DATASOURCE_PASSWORD=Web_pass123!
Environment=SPRING_JPA_HIBERNATE_DDL_AUTO=update

[Install]
WantedBy=multi-user.target
"""

                    writeFile file: "service.tmp", text: serviceFile

                    sh """
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
                    sudo systemctl restart ${SERVICE_NAME}.service
                    sudo systemctl status ${SERVICE_NAME}.service --no-pager || true
                """
            }
        }
    }

    post {
        always {
            echo "Build finished for branch ${env.BRANCH_NAME} on port ${PORT}"
        }
    }
}
