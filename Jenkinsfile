pipeline {
    agent any

    environment {
        MAVEN_HOME = "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11"
        BASE_DEPLOY_DIR = "/var/lib/jenkins/meditrack"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn clean package -DskipTests -T 1C"
            }
        }

        stage('Deploy') {
            steps {
                script {
                    BRANCH = env.BRANCH_NAME
                    DEPLOY_DIR = "${BASE_DEPLOY_DIR}/${BRANCH}"

                    SERVER_PORT = (BRANCH == 'main') ? 9090 :
                                  (BRANCH == 'test') ? 9091 :
                                  (BRANCH == 'features') ? 9092 : 9093

                    SERVICE_NAME="meditrack-${BRANCH}"

                    sh "mkdir -p ${DEPLOY_DIR}"
                    sh "cp target/meditrack-*.jar ${DEPLOY_DIR}/meditrack-0.0.1-SNAPSHOT.jar"

                    // Systemd Service nur erzeugen, wenn JAR existiert
                    sh """
                    if [ -f ${DEPLOY_DIR}/meditrack-0.0.1-SNAPSHOT.jar ]; then
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

                        sudo systemctl daemon-reload
                        sudo systemctl enable ${SERVICE_NAME}.service
                        sudo systemctl restart ${SERVICE_NAME}.service
                    else
                        echo "❌ JAR nicht gefunden, Service wird nicht gestartet"
                    fi
                    """
                }
            }
        }
    }
}
