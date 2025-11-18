pipeline {
    agent any

    environment {
        MAVEN_HOME = "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11"
        BASE_DEPLOY_DIR = "/var/lib/jenkins/meditrack"
        MAIN_BRANCH = "main"
    }

    stages {
        stage('Checkout Jenkinsfile from Main') {
            steps {
                script {
                    echo "Fetching Jenkinsfile from main branch..."
                    sh "git fetch origin ${MAIN_BRANCH}:${MAIN_BRANCH}_tmp"
                    sh "git checkout ${MAIN_BRANCH}_tmp -- Jenkinsfile"
                }
            }
        }

        stage('Checkout Code of Current Branch') {
            steps {
                script {
                    BRANCH = env.BRANCH_NAME
                    echo "Building code of branch: ${BRANCH}"

                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: "*/${BRANCH}"]],
                        userRemoteConfigs: [[url: "https://github.com/Nadolze/MediTrack.git"]]
                    ])
                }
            }
        }

        stage('Setup Environment') {
            steps {
                script {
                    DEPLOY_DIR = "${BASE_DEPLOY_DIR}/${BRANCH}"

                    SERVER_PORT = (BRANCH == 'main') ? 9090 :
                                  (BRANCH == 'test') ? 9091 :
                                  (BRANCH == 'features') ? 9092 : 9093

                    echo "Deploy dir: ${DEPLOY_DIR}"
                    echo "Server Port: ${SERVER_PORT}"

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
                    sh "sudo systemctl stop meditrack-${BRANCH}.service || true"
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
