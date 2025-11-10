pipeline {
    agent any

    environment {
        BASE_DEPLOY_DIR = isUnix() ? "/opt/meditrack" : "C:\\MediTrack"
        MAVEN_HOME = isUnix() ? "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11" : "C:\\Maven"
    }

    stages {
        stage('Clean Build Artifacts') {
            steps {
                script {
                    // Nur alte Build-Artefakte löschen, Repository bleibt
                    if (isUnix()) {
                        sh "rm -rf target"
                    } else {
                        bat "rmdir /s /q target"
                    }
                    sh "mkdir -p ${BASE_DEPLOY_DIR}/${env.BRANCH_NAME}"
                }
            }
        }
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh "${MAVEN_HOME}/bin/mvn clean package -DskipTests"
                    } else {
                        bat "${MAVEN_HOME}\\bin\\mvn clean package -DskipTests"
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    def port = env.BRANCH_NAME == 'main' ? 9090 : findFreePort()
                    def deployDir = "${BASE_DEPLOY_DIR}/${env.BRANCH_NAME}"

                    if (isUnix()) {
                        sh "cp target/meditrack-*.jar ${deployDir}/"
                        sh """
                        cat <<EOF | sudo tee /etc/systemd/system/meditrack-${env.BRANCH_NAME}.service
                        [Unit]
                        Description=MediTrack Spring Boot Application for ${env.BRANCH_NAME}
                        After=network.target

                        [Service]
                        User=jenkins
                        ExecStart=/usr/bin/java -jar ${deployDir}/meditrack-0.0.1-SNAPSHOT.jar --server.port=${port}
                        Restart=always

                        [Install]
                        WantedBy=multi-user.target
                        EOF
                        """
                        sh "sudo systemctl daemon-reload"
                        sh "sudo systemctl enable meditrack-${env.BRANCH_NAME}.service"
                        sh "sudo systemctl restart meditrack-${env.BRANCH_NAME}.service"
                    } else {
                        bat "copy target\\meditrack-*.jar ${deployDir}\\"
                        // Windows Service erzeugen z.B. mit NSSM oder PowerShell
                        echo "Hier Windows Service erstellen (z.B. mit NSSM) für Branch ${env.BRANCH_NAME} auf Port ${port}"
                    }

                    echo "✅ Deployed branch ${env.BRANCH_NAME} on port ${port}"
                }
            }
        }
    }
}

// Hilfsfunktion für freie Ports auf Unix
def findFreePort() {
    def port
    for (portCandidate in 9091..9199) {
        def result = sh(script: "ss -tuln | grep :${portCandidate}", returnStatus: true)
        if (result != 0) {
            port = portCandidate
            break
        }
    }
    if (port == null) error "No free port found!"
    return port
}
