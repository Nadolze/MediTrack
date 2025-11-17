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

                    // Deploy in Jenkins-Home
                    BASE_DEPLOY_DIR = "${env.JENKINS_HOME}/meditrack"
                    BRANCH = env.BRANCH_NAME
                    DEPLOY_DIR = "${BASE_DEPLOY_DIR}/${BRANCH}"

                    // Port-Zuweisung
                    SERVER_PORT = (BRANCH == 'main') ? 9090 : (BRANCH == 'test' ? 9091 : 9092)

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
                        sh "mkdir -p ${DEPLOY_DIR}"
                    } else {
                        bat "rmdir /s /q target"
                        bat "mkdir ${DEPLOY_DIR}"
                    }
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
                    if (isUnix()) {
                        sh "cp target/meditrack-*.jar ${DEPLOY_DIR}/"
                        sh """
                        cat <<EOF > ${DEPLOY_DIR}/meditrack-${BRANCH}.service
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
                        sh "systemctl --user daemon-reload"
                        sh "systemctl --user enable ${DEPLOY_DIR}/meditrack-${BRANCH}.service"
                        sh "systemctl --user restart ${DEPLOY_DIR}/meditrack-${BRANCH}.service"
                    } else {
                        bat "copy target\\meditrack-*.jar ${DEPLOY_DIR}\\"
                        echo "Windows Service für Branch ${BRANCH} auf Port ${SERVER_PORT} erstellen (z.B. mit NSSM)"
                    }

                    echo "✅ Deployed branch ${BRANCH} on port ${SERVER_PORT}"
                }
            }
        }
    }
}
