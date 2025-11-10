pipeline {
    agent any
    environment {
        MAVEN_HOME = isUnix() ? "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11" : "C:\\Maven"
        BASE_DEPLOY_DIR = isUnix() ? "/opt/meditrack" : "C:\\MediTrack"
        MAIN_PORT = 9090
        START_PORT = 9091
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
                    // Lösche nur alte Build-Artefakte, Repo bleibt
                    if (isUnix()) {
                        sh "rm -rf target"
                        sh "mkdir -p ${BASE_DEPLOY_DIR}/${env.BRANCH_NAME}"
                    } else {
                        bat "rmdir /s /q target"
                        bat "mkdir ${BASE_DEPLOY_DIR}\\${env.BRANCH_NAME}"
                    }
                }
            }
        }


        stage('Build') {
            steps {
                script {
                    def branch = env.BRANCH_NAME
                    sh "${MAVEN_HOME}/bin/mvn clean package -DskipTests"
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    def branch = env.BRANCH_NAME
                    def port = branch == 'main' ? MAIN_PORT : findFreePort(START_PORT)
                    def deployDir = "${BASE_DEPLOY_DIR}/${branch}"
                    sh "cp target/meditrack-*.jar ${deployDir}/"

                    if (isUnix()) {
                        def serviceFile = "/etc/systemd/system/meditrack-${branch}.service"
                        sh """
                        echo '[Unit]
                        Description=MediTrack Spring Boot Application for ${branch}
                        After=network.target

                        [Service]
                        User=jenkins
                        ExecStart=/usr/bin/java -jar ${deployDir}/meditrack-0.0.1-SNAPSHOT.jar --server.port=${port}
                        Restart=always

                        [Install]
                        WantedBy=multi-user.target' | sudo tee ${serviceFile}

                        sudo systemctl daemon-reload
                        sudo systemctl enable meditrack-${branch}.service
                        sudo systemctl restart meditrack-${branch}.service
                        """
                    } else {
                        // Windows: nssm Service erstellen oder restarten
                        bat """
                        nssm install MediTrack-${branch} ${MAVEN_HOME}\\bin\\java.exe -jar ${deployDir}\\meditrack-0.0.1-SNAPSHOT.jar --server.port=${port}
                        nssm start MediTrack-${branch}
                        """
                    }
                }
            }
        }
    }
}

// Hilfsfunktion zum Finden eines freien Ports
def findFreePort(startPort) {
    def port = startPort
    while (true) {
        try {
            def socket = new ServerSocket(port)
            socket.close()
            return port
        } catch (Exception e) {
            port++
        }
    }
}
