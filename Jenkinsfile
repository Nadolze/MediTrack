pipeline {
    agent any

    environment {
        MAVEN_HOME = isUnix() ? "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11" : "C:\\Maven"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Clean Workspace') {
            steps {
                script {
                    // Nur alte Build-Artefakte löschen, nicht den Git-Code
                    sh "rm -rf target/"
                }
            }
        }

        stage('Setup') {
            steps {
                script {
                    branch = env.BRANCH_NAME
                    DEPLOY_DIR = isUnix() ? "/opt/meditrack/${branch}" : "C:\\MediTrack\\${branch}"
                    if (branch == 'main') {
                        SERVER_PORT = 9090
                    } else {
                        BASE_PORT = 9091
                        usedPorts = sh(script: "systemctl list-units --type=service | grep meditrack- | awk '{print \$1}' | sed -E 's/[^0-9]*([0-9]+)\\.service/\\1/'", returnStdout: true).trim().split("\n")
                        while (usedPorts.contains(BASE_PORT.toString())) { BASE_PORT++ }
                        SERVER_PORT = BASE_PORT
                    }
                    echo "Branch: ${branch}"
                    echo "Deploy dir: ${DEPLOY_DIR}"
                    echo "Server Port: ${SERVER_PORT}"
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
                    sh "rm -rf ${DEPLOY_DIR} && mkdir -p ${DEPLOY_DIR}"
                    sh "cp target/meditrack-0.0.1-SNAPSHOT.jar ${DEPLOY_DIR}/meditrack-0.0.1-SNAPSHOT.jar"

                    sh """
                        SERVICE_FILE=/etc/systemd/system/meditrack-${branch}.service
                        cp /etc/systemd/system/meditrack-template.service \$SERVICE_FILE
                        sed -i 's/%i/${branch}/g' \$SERVICE_FILE
                        sed -i 's/%p/${SERVER_PORT}/g' \$SERVICE_FILE
                        systemctl daemon-reload
                        systemctl enable meditrack-${branch}.service
                        systemctl restart meditrack-${branch}.service
                    """
                }
            }
        }
    }

    post {
        success {
            echo "✅ Deployment erfolgreich für Branch ${env.BRANCH_NAME}"
        }
        failure {
            echo "❌ Deployment fehlgeschlagen für Branch ${env.BRANCH_NAME}"
        }
    }
}
