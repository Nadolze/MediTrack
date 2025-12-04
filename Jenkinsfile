pipeline {
    agent any

    environment {
        MAVEN_HOME = "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11"
        BASE_DEPLOY_DIR = "/var/lib/jenkins/meditrack"
    }

    stages {
        stage('Kill old MediTrack instances') {
            steps {
                sh '''
                    echo "🔪 Killing MediTrack processes on ports 9090–9099..."
                    for port in $(seq 9090 9099); do
                        pid=$(lsof -t -i:$port || true)
                        if [ ! -z "$pid" ]; then
                            echo "Killing PID $pid on port $port"
                            kill -9 $pid || true
                        fi
                    done
                    echo "🕑 Waiting 5 seconds before continuing..."
                    sleep 5
                '''
            }
        }

        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Setup Environment') {
            steps {
                script {
                    BRANCH = env.BRANCH_NAME ?: sh(script: "git rev-parse --abbrev-ref HEAD", returnStdout: true).trim()
                    DEPLOY_DIR = "${BASE_DEPLOY_DIR}/${BRANCH}"

                    // Port je nach Branch
                    SERVER_PORT = (BRANCH == 'main') ? 9090 :
                                  (BRANCH == 'test') ? 9091 :
                                  (BRANCH == 'features') ? 9092 :
                                  9093 // alle anderen starten bei 9093 (einfachheitshalber)

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
                sh """
                    echo "🔨 Building JAR..."
                    ${MAVEN_HOME}/bin/mvn clean package -DskipTests
                """
            }
        }

        stage('Deploy') {
            steps {
                script {
                    SERVICE_NAME="meditrack-${BRANCH}"

                    // Alten Service stoppen
                    sh "sudo systemctl stop ${SERVICE_NAME}.service || true"

                    // JAR kopieren
                    sh "cp target/meditrack-*.jar ${DEPLOY_DIR}/"

                    // systemd-Service erstellen/ersetzen
                    sh """
                    cat <<EOF | sudo tee /etc/systemd/system/${SERVICE_NAME}.service
                    [Unit]
                    Description=MediTrack Spring Boot Application (${BRANCH})
                    After=network.target

                    [Service]
                    User=jenkins
                    ExecStart=/usr/bin/java -Xms256m -Xmx512m -jar ${DEPLOY_DIR}/meditrack-0.0.1-SNAPSHOT.jar --server.port=${SERVER_PORT}
                    Restart=always
                    LimitNOFILE=4096
                    LimitNPROC=500
                    CPUQuota=50%

                    [Install]
                    WantedBy=multi-user.target
                    EOF
                    """

                    sh "sudo systemctl daemon-reload"
                    sh "sudo systemctl enable ${SERVICE_NAME}.service"
                    sh "sudo systemctl restart ${SERVICE_NAME}.service"

                    echo "✅ Deployed branch ${BRANCH} on port ${SERVER_PORT}"
                }
            }
        }

        stage('Show Running') {
            steps {
                sh "sudo systemctl status meditrack-${BRANCH}.service --no-pager || true"
            }
        }
    }
}
