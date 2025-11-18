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

        stage('Setup Environment') {
            steps {
                script {
                    BRANCH = env.BRANCH_NAME
                    DEPLOY_DIR = "${BASE_DEPLOY_DIR}/${BRANCH}"

                    // Branch-Port Mapping
                    SERVER_PORT = (BRANCH == 'main') ? 9090 :
                                  (BRANCH == 'test') ? 9091 :
                                  (BRANCH == 'features') ? 9092 : 9093

                    echo "Branch: ${BRANCH}"
                    echo "Deploy dir: ${DEPLOY_DIR}"
                    echo "Server Port: ${SERVER_PORT}"
                    echo "Maven Home: ${MAVEN_HOME}"

                    sh "mkdir -p ${DEPLOY_DIR}"
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
                    SERVICE_NAME="meditrack-${BRANCH}"

                    // Stop existing service (ignore errors)
                    sh "sudo systemctl stop ${SERVICE_NAME}.service || true"

                    // Copy new JAR
                    sh "cp target/meditrack-*.jar ${DEPLOY_DIR}/"

                    // Create systemd service with limits
                    sh """
                    cat <<EOF | sudo tee /etc/systemd/system/${SERVICE_NAME}.service
[Unit]
Description=MediTrack Spring Boot Application for ${BRANCH}
After=network.target

[Service]
User=jenkins
ExecStart=/usr/bin/java -Xms256m -Xmx512m -XX:ActiveProcessorCount=2 -jar ${DEPLOY_DIR}/meditrack-0.0.1-SNAPSHOT.jar --server.port=${SERVER_PORT}
Restart=always
CPUQuota=50%

[Install]
WantedBy=multi-user.target
EOF
                    """

                    sh "sudo systemctl daemon-reload"
                    sh "sudo systemctl enable ${SERVICE_NAME}.service"
                    sh "sudo systemctl restart ${SERVICE_NAME}.service"

                    echo "✅ Deployed branch ${BRANCH} on port ${SERVER_PORT} with CPU/RAM limits"
                }
            }
        }
    }
}
