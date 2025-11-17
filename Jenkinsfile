pipeline {
    agent any

    environment {
        BASE_DEPLOY_DIR = "/opt/meditrack"
        MAVEN_HOME = "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11"
        BRANCH = "${env.BRANCH_NAME}"
        SERVER_PORT = BRANCH == 'main' ? 9090 : (BRANCH == 'test' ? 9091 : 9092)
        DEPLOY_DIR = "${BASE_DEPLOY_DIR}/${BRANCH}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Merge Main into Test (falls Test)') {
            when {
                expression { BRANCH == 'test' }
            }
            steps {
                script {
                    sh "git fetch origin main"
                    sh "git merge origin/main --no-ff -m 'Merge main into test'"
                }
            }
        }

        stage('Clean Build Artifacts') {
            steps {
                script {
                    sh "rm -rf target"
                    sh "mkdir -p ${DEPLOY_DIR}"
                }
            }
        }

        stage('Build') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn clean package -DskipTests"
            }
        }

        stage('Deploy') {
            steps {
                script {
                    sh "cp target/meditrack-*.jar ${DEPLOY_DIR}/"
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
