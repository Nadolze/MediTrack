pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Setup Environment') {
            steps {
                script {
                    BRANCH = env.BRANCH_NAME
                    BASE_DEPLOY_DIR = "/opt/meditrack"
                    DEPLOY_DIR = "${BASE_DEPLOY_DIR}/${BRANCH}"

                    // Branch-Port-Mapping
                    switch(BRANCH) {
                        case 'main':
                            SERVER_PORT = 9090
                            break
                        case 'test':
                            SERVER_PORT = 9091
                            break
                        default:
                            SERVER_PORT = 9092
                    }

                    echo "Branch: ${BRANCH}"
                    echo "Deploy dir: ${DEPLOY_DIR}"
                    echo "Server Port: ${SERVER_PORT}"
                }
            }
        }

        stage('Clean Build Artifacts') {
            steps {
                script {
                    echo "Cleaning old build artifacts..."
                    sh "rm -rf target"
                    sh "mkdir -p ${DEPLOY_DIR}"
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    echo "Building project..."
                    sh "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11/bin/mvn clean package -DskipTests"
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    echo "Deploying branch ${BRANCH} on port ${SERVER_PORT}..."
                    sh "cp target/meditrack-*.jar ${DEPLOY_DIR}/"

                    // Systemd Service erstellen/aktualisieren
                    sh """
                    cat <<EOF | sudo tee /etc/systemd/system/meditrack-${BRANCH}.service
                    [Unit]
                    Description=MediTrack Spring Boot Application for ${BRANCH}
                    After=network.target

                    [Service]
                    User=jenkins
                    ExecStart=/usr/bin/java -jar ${DEPLOY_DIR}/meditrack-0.0.1-SNAPSHOT.jar --server.port=${SERVER_PORT}
                    Restart=always

                    [Install]
                    WantedBy=multi-user.target
                    EOF
                    """

                    sh "sudo systemctl daemon-reload"
                    sh "sudo systemctl enable meditrack-${BRANCH}.service"
                    sh "sudo systemctl restart meditrack-${BRANCH}.service"

                    echo "✅ Deployed branch ${BRANCH} on port ${SERVER_PORT}"
                }
            }
        }
    }
}
