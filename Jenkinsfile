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
                    // OS-abhängige Variablen
                    def isUnix = isUnix()
                    MAVEN_HOME = isUnix ? "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11" : "C:\\Maven"
                    BASE_DEPLOY_DIR = isUnix ? "/opt/meditrack" : "C:\\MediTrack"
                    BRANCH = env.BRANCH_NAME
                    DEPLOY_DIR = "${BASE_DEPLOY_DIR}/${BRANCH}"
                    SERVER_PORT = (BRANCH == 'main') ? 9090 : findFreePort()

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
                    // Nur alte Build-Artefakte löschen, Repo bleibt erhalten
                    if (isUnix()) {
                        sh "rm -rf target"
                    } else {
                        bat "rmdir /s /q target"
                    }
                    sh "mkdir -p ${DEPLOY_DIR}"
                }
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
                            def branch = env.BRANCH_NAME
                            def deployDir = "${BASE_DEPLOY_DIR}/${branch}"
                            def jarFile = "target/meditrack-0.0.1-SNAPSHOT.jar"

                            // Portzuweisung: main = 9090, alle anderen freie Ports
                            def port = (branch == 'main') ? 9090 : 9000 + Math.abs(branch.hashCode() % 1000)

                            sh "cp ${jarFile} ${deployDir}/"

                            def serviceFile = "/etc/systemd/system/meditrack-${branch}.service"

                            sh """
                            echo "[Unit]
                            Description=MediTrack Spring Boot Application for ${branch}
                            After=network.target

                            [Service]
                            User=jenkins
                            ExecStart=/usr/bin/java -jar ${deployDir}/meditrack-0.0.1-SNAPSHOT.jar --server.port=${port}
                            Restart=always

                            [Install]
                            WantedBy=multi-user.target" | sudo tee ${serviceFile}
                            """

                            sh "sudo systemctl daemon-reload"
                            sh "sudo systemctl enable meditrack-${branch}.service"
                            sh "sudo systemctl restart meditrack-${branch}.service"

                            echo "✅ Deployed branch ${branch} on port ${port}"
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
