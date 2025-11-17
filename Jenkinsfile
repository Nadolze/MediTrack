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
                    // OS check
                    def isUnix = isUnix()

                    // Branch
                    def BRANCH = env.BRANCH_NAME

                    // Deployment directories
                    def BASE_DEPLOY_DIR = isUnix ? "/var/lib/jenkins/meditrack" : "C:\\MediTrack"
                    def DEPLOY_DIR = "${BASE_DEPLOY_DIR}/${BRANCH}"

                    // Server Ports
                    def SERVER_PORT = BRANCH == 'main' ? 9090 : (BRANCH == 'test' ? 9091 : 9092)

                    // Maven Home
                    def MAVEN_HOME = tool name: 'Maven_3.9.11', type: 'hudson.tasks.Maven$MavenInstallation'

                    // Export variables to environment
                    env.BRANCH = BRANCH
                    env.DEPLOY_DIR = DEPLOY_DIR
                    env.SERVER_PORT = "${SERVER_PORT}"
                    env.MAVEN_HOME = MAVEN_HOME

                    echo "Branch: ${BRANCH}"
                    echo "Deploy Dir: ${DEPLOY_DIR}"
                    echo "Server Port: ${SERVER_PORT}"
                    echo "Maven Home: ${MAVEN_HOME}"

                    // Ensure deploy dir exists
                    if (isUnix) {
                        sh "mkdir -p ${DEPLOY_DIR}"
                    } else {
                        bat "mkdir ${DEPLOY_DIR}"
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    // Non-resumable Maven call
                    if (isUnix()) {
                        sh "${env.MAVEN_HOME}/bin/mvn clean package -DskipTests"
                    } else {
                        bat "${env.MAVEN_HOME}\\bin\\mvn clean package -DskipTests"
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    if (isUnix()) {
                        // Copy JAR
                        sh "cp target/meditrack-*.jar ${env.DEPLOY_DIR}/"

                        // Write Systemd service
                        sh """
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
                        """

                        sh "sudo systemctl daemon-reload"
                        sh "sudo systemctl enable meditrack-${env.BRANCH}.service"
                        sh "sudo systemctl restart meditrack-${env.BRANCH}.service"
                    } else {
                        bat "copy target\\meditrack-*.jar ${env.DEPLOY_DIR}\\"
                        echo "Windows deployment: bitte NSSM oder ähnliches für Service nutzen"
                    }

                    echo "✅ Deployed branch ${env.BRANCH} on port ${env.SERVER_PORT}"
                }
            }
        }
    }
}
