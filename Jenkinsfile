pipeline {
    agent any

    environment {
        MAVEN_HOME = "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11"
        BASE_DEPLOY_DIR = "/var/lib/jenkins/meditrack"
    }

    stages {

        stage('Kill all MediTrack servers') {
            steps {
                echo "🔪 Killing all MediTrack instances on ports 9090-9099..."
                sh '''
                    for port in $(seq 9090 9099); do
                        pid=$(lsof -t -i:$port || true)
                        if [ ! -z "$pid" ]; then
                            echo "Killing PID $pid on port $port"
                            kill -9 $pid
                        fi
                    done
                    echo "🕑 Waiting 10 seconds to ensure all servers stopped..."
                    sleep 10

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
                    def branch = env.BRANCH_NAME
                    def deployDir = "${BASE_DEPLOY_DIR}/${branch}"
                    def serverPort = 9093 // default port

                    if (branch == 'main') {
                        serverPort = 9090
                    } else if (branch == 'test') {
                        serverPort = 9091
                    } else if (branch == 'features') {
                        serverPort = 9092
                    } // sonst bleibt default 9093+

                    echo "Branch: ${branch}"
                    echo "Deploy dir: ${deployDir}"
                    echo "Server Port: ${serverPort}"
                    echo "Maven Home: ${MAVEN_HOME}"

                    sh "mkdir -p ${deployDir}"

                    // Variablen global verfügbar machen
                    env.DEPLOY_DIR = deployDir
                    env.SERVER_PORT = serverPort.toString()
                }
            }
        }

        stage('Build') {
            steps {
                echo "🔨 Building JAR..."
                sh "${MAVEN_HOME}/bin/mvn clean package -DskipTests"
            }
        }

        stage('Deploy') {
            steps {
                script {
                    echo "🚀 Deploying branch ${env.BRANCH_NAME} on port ${env.SERVER_PORT}..."
                    sh """
                        cp target/meditrack-*.jar ${env.DEPLOY_DIR}/

                        cat <<EOF | sudo tee /etc/systemd/system/meditrack-${env.BRANCH_NAME}.service
                        [Unit]
                        Description=MediTrack Spring Boot Application for ${env.BRANCH_NAME}
                        After=network.target

                        [Service]
                        User=jenkins
                        ExecStart=/usr/bin/java -Xms128m -Xmx512m -jar ${env.DEPLOY_DIR}/meditrack-0.0.1-SNAPSHOT.jar --server.port=${env.SERVER_PORT}
                        Restart=on-failure
                        CPUQuota=50%
                        MemoryMax=512M

                        [Install]
                        WantedBy=multi-user.target
                        EOF

                        sudo systemctl daemon-reload
                        sudo systemctl enable meditrack-${env.BRANCH_NAME}.service
                        sudo systemctl restart meditrack-${env.BRANCH_NAME}.service
                    """
                    echo "✅ Deployed branch ${env.BRANCH_NAME} on port ${env.SERVER_PORT}"
                }
            }
        }

        stage('Show Running') {
            steps {
                sh "ps -ef | grep '[m]editrack'"
            }
        }
    }
}
