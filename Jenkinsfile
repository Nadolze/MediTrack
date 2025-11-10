pipeline {
    agent any

    stages {


        stage('Clean Workspace') {
            steps {
                echo "Cleaning workspace AFTER checkout"
                deleteDir()
            }
        }

        stage('Checkout') {
                    steps {
                        echo "Checking out branch ${env.BRANCH_NAME}"
                        checkout scm
                    }
                }

        stage('Setup Environment') {
            steps {
                script {
                    // OS-spezifische Pfade
                    MAVEN_HOME = isUnix() ? "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11" : "C:\\Maven"
                    BASE_DEPLOY_DIR = isUnix() ? "/opt/meditrack" : "C:\\MediTrack"

                    BRANCH = env.BRANCH_NAME
                    DEPLOY_DIR = "${BASE_DEPLOY_DIR}/${BRANCH}"

                    // Port bestimmen
                    SERVER_PORT = (BRANCH == 'main') ? 9090 : findFreePort()

                    echo "Branch: ${BRANCH}"
                    echo "Deploy dir: ${DEPLOY_DIR}"
                    echo "Server Port: ${SERVER_PORT}"
                    echo "Maven Home: ${MAVEN_HOME}"
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    sh "${MAVEN_HOME}/bin/mvn clean package -DskipTests"
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    sh "mkdir -p ${DEPLOY_DIR}"
                    sh "cp target/meditrack-0.0.1-SNAPSHOT.jar ${DEPLOY_DIR}/"

                    SERVICE_FILE = "/etc/systemd/system/meditrack-${BRANCH}.service"
                    sh """
                    echo '[Unit]
                    Description=MediTrack Spring Boot Application for ${BRANCH}
                    After=network.target

                    [Service]
                    User=jenkins
                    ExecStart=/usr/bin/java -jar ${DEPLOY_DIR}/meditrack-0.0.1-SNAPSHOT.jar --server.port=${SERVER_PORT}
                    Restart=always

                    [Install]
                    WantedBy=multi-user.target' | sudo tee ${SERVICE_FILE}
                    """

                    sh "sudo systemctl daemon-reload"
                    sh "sudo systemctl enable meditrack-${BRANCH}.service"
                    sh "sudo systemctl restart meditrack-${BRANCH}.service"
                }
            }
        }
    }
}

// Hilfsfunktion zum freien Port (Unix only)
def findFreePort() {
    def port = 9100
    while (true) {
        def isFree = sh(script: "ss -tln | grep -q ':${port}' || echo free", returnStdout: true).trim()
        if (isFree == 'free') return port
        port += 1
    }
}
