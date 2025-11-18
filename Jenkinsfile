pipeline {
    agent any

    environment {
        MAVEN_HOME = "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11"
        BASE_DEPLOY_DIR = "/var/lib/jenkins/meditrack"
    }

    stages {
        stage('Checkout Main Jenkinsfile') {
            steps {
                script {
                    echo "Checking out Jenkinsfile from main branch..."
                    sh "git fetch origin main"
                    sh "git checkout origin/main -- Jenkinsfile"
                }
            }
        }

        stage('Checkout Branch') {
            steps {
                checkout scm
            }
        }

        stage('Setup Environment') {
            steps {
                script {
                    // Branch erkennen
                    BRANCH = env.BRANCH_NAME
                    DEPLOY_DIR = "${BASE_DEPLOY_DIR}/${BRANCH}"

                    // Port je nach Branch
                    SERVER_PORT = (BRANCH == 'main') ? 9090 :
                                  (BRANCH == 'test') ? 9091 :
                                  (BRANCH == 'features') ? 9092 : 9093

                    echo "Branch: ${BRANCH}"
                    echo "Deploy dir: ${DEPLOY_DIR}"
                    echo "Server Port: ${SERVER_PORT}"
                    echo "Maven Home: ${MAVEN_HOME}"

                    // Deploy-Verzeichnis anlegen
                    sh "mkdir -p ${DEPLOY_DIR}"
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    sh "${MAVEN_HOME}/bin/mvn clean package -DskipTests -T 1C"
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    SERVICE_NAME="meditrack-${BRANCH}"

                    // Alten Service stoppen (falls existiert)
                    sh "sudo systemctl stop ${SERVICE_NAME} || true"

                    // JAR kopieren
                    sh "cp target/meditrack-*.jar ${DEPLOY_DIR}/meditrack-0.0.1-SNAPSHOT.jar"

                    // Systemd-Service erstellen / ersetzen mit Ressourcenlimits
                    sh """
                    cat <<EOF | sudo tee /etc/systemd/system/${SERVICE_NAME}.service
                    [Unit]
                    Description=MediTrack Spring Boot Application for ${BRANCH}
                    After=network.target

                    [Service]
                    User=jenkins
                    ExecStart=/usr/bin/java -jar ${DEPLOY_DIR}/meditrack-0.0.1-SNAPSHOT.jar --server.port=${SERVER_PORT}
                    Restart=always
                    CPUQuota=50%
                    MemoryMax=512M

                    [Install]
                    WantedBy=multi-user.target
                    EOF
                    """

                    sh "sudo systemctl daemon-reload"
                    sh "sudo systemctl enable ${SERVICE_NAME}.service"
                    sh "sudo systemctl restart ${SERVICE_NAME}.service"

                    echo "✅ Deployed branch ${BRANCH} on port ${SERVER_PORT} with systemd resource limits"
                }
            }
        }
    }
}
