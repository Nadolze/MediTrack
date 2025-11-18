pipeline {
    agent any

    environment {
        MAVEN_HOME = "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11"
        BASE_DEPLOY_DIR = "/var/lib/jenkins/meditrack"
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Setup') {
            steps {
                script {
                    BRANCH = env.BRANCH_NAME
                    DEPLOY_DIR = "${BASE_DEPLOY_DIR}/${BRANCH}"

                    SERVER_PORT = (BRANCH == 'main') ? 9090 :
                                  (BRANCH == 'test') ? 9091 : 9092

                    echo "Branch: ${BRANCH}"
                    echo "Deploy dir: ${DEPLOY_DIR}"
                    echo "Server Port: ${SERVER_PORT}"

                    sh "mkdir -p ${DEPLOY_DIR}"
                }
            }
        }

        stage('Build') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn clean package -DskipTests"
            }
        }

        stage('Deploy & Run') {
            steps {
                // Stoppen des alten Services (falls vorhanden)
                sh "sudo systemctl stop meditrack-${BRANCH}.service || true"

                // JAR kopieren
                sh "sudo cp target/meditrack-*.jar ${BASE_DEPLOY_DIR}/${BRANCH}/"

                // Service erstellen / ersetzen
                sh """
                sudo bash -c 'cat <<EOF > /etc/systemd/system/meditrack-${BRANCH}.service
[Unit]
Description=MediTrack Spring Boot Application for ${BRANCH}
After=network.target

[Service]
User=jenkins
ExecStart=/usr/bin/java -jar ${BASE_DEPLOY_DIR}/${BRANCH}/meditrack-0.0.1-SNAPSHOT.jar --server.port=${SERVER_PORT}
Restart=always

[Install]
WantedBy=multi-user.target
EOF'
                """

                // systemd neu laden und starten
                sh "sudo systemctl daemon-reload"
                sh "sudo systemctl enable meditrack-${BRANCH}.service"
                sh "sudo systemctl restart meditrack-${BRANCH}.service"

                echo "✅ Branch ${BRANCH} deployed on port ${SERVER_PORT}"
            }
        }
    }
}
