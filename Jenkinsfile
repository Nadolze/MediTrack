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
                    // Branch-Namen
                    def BRANCH = env.BRANCH_NAME
                    // Deploy-Ordner in Jenkins Home
                    def BASE_DEPLOY_DIR = "${env.JENKINS_HOME}/meditrack"
                    def DEPLOY_DIR = "${BASE_DEPLOY_DIR}/${BRANCH}"

                    // Server-Port je Branch
                    def SERVER_PORT = BRANCH == 'main' ? 9090 :
                                      (BRANCH == 'test' ? 9091 : 9092)

                    // Maven über Jenkins Tool
                    def MAVEN_HOME = tool name: 'Maven_3.9.11', type: 'hudson.tasks.Maven$MavenInstallation'

                    // Variablen für andere Stages
                    env.BRANCH = BRANCH
                    env.DEPLOY_DIR = DEPLOY_DIR
                    env.SERVER_PORT = SERVER_PORT
                    env.MAVEN_HOME = MAVEN_HOME

                    echo "Branch: ${BRANCH}"
                    echo "Deploy dir: ${DEPLOY_DIR}"
                    echo "Server Port: ${SERVER_PORT}"
                    echo "Maven Home: ${MAVEN_HOME}"
                }
            }
        }

        stage('Clean Build Artifacts') {
            steps {
                script {
                    sh "rm -rf target"
                    sh "mkdir -p ${env.DEPLOY_DIR}"
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    sh "${env.MAVEN_HOME}/bin/mvn clean package -DskipTests"
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    sh """
                        cp target/meditrack-*.jar ${env.DEPLOY_DIR}/
                        cat <<EOF | sudo tee /etc/systemd/system/meditrack-${env.BRANCH}.service
[Unit]
Description=MediTrack Spring Boot Application for ${env.BRANCH}
After=network.target

[Service]
User=jenkins
ExecStart=/usr/bin/java -jar ${env.DEPLOY_DIR}/meditrack-0.0.1-SNAPSHOT.jar --server.port=${env.SERVER_PORT}
Restart=always

[Install]
WantedBy=multi-user.target
EOF
                        sudo systemctl daemon-reload
                        sudo systemctl enable meditrack-${env.BRANCH}.service
                        sudo systemctl restart meditrack-${env.BRANCH}.service
                    """
                    echo "✅ Deployed branch ${env.BRANCH} on port ${env.SERVER_PORT}"
                }
            }
        }
    }
}
