pipeline {
    agent any

    environment {
        MAVEN_HOME = "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11"
        BASE_DEPLOY_DIR = "/opt/meditrack"
    }

    stages {
        stage('Checkout SCM') {
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
                                  (BRANCH == 'test') ? 9091 : 9092

                    echo "Branch: ${BRANCH}"
                    echo "Deploy dir: ${DEPLOY_DIR}"
                    echo "Server Port: ${SERVER_PORT}"
                    echo "Maven Home: ${MAVEN_HOME}"

                    // Deploy-Verzeichnis anlegen (sudo nötig)
                    sh "sudo mkdir -p ${DEPLOY_DIR}"
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
                    // Alten Service stoppen, falls er existiert
                    sh "sudo systemctl stop meditrack-${BRANCH}.service || true"

                    // JAR kopieren
                    sh "sudo cp target/meditrack-*.jar ${DEPLOY_DIR}/"

                    // Systemd-Service erstellen / ersetzen
                    sh """
                    sudo bash -c 'cat <<EOF > /etc/systemd/system/meditrack-${BRANCH}.service
                    [Unit]
                    Description=MediTrack Spring Boot Application for ${BRANCH}
                    After=network.target

                    [Service]
                    User=jenkins
                    ExecStart=/usr/bin/java -jar ${DEPLOY_DIR}/meditrack-0.0.1-SNAPSHOT.jar --server.port=${SERVER_PORT}
                    Restart=always

                    [Install]
                    WantedBy=multi-user.target
                    EOF'
                    """

                    // systemd neu laden und Service starten
                    sh "sudo systemctl daemon-reload"
                    sh "sudo systemctl enable meditrack-${BRANCH}.service"
                    sh "sudo systemctl restart meditrack-${BRANCH}.service"

                    echo "✅ Deployed branch ${BRANCH} on port ${SERVER_PORT}"
                }
            }
        }
    }
}
