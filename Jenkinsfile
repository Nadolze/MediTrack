pipeline {
    agent any

    environment {
        MAVEN_HOME = "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11"
        BASE_DEPLOY_DIR = "/var/lib/jenkins/meditrack"
    }

    stages {

        stage('Stop old servers') {
            steps {
                script {
                    echo "🔪 Stopping any existing MediTrack instances on ports 9090-9099..."
                    // Ports 9090-9099 prüfen und Prozesse killen
                    sh '''
                    for port in $(seq 9090 9099); do
                        pid=$(lsof -t -i:$port || true)
                        if [ ! -z "$pid" ]; then
                            echo "Killing PID $pid on port $port"
                            kill -9 $pid
                        fi
                    done
                    echo "🕑 Waiting 5 seconds to ensure all servers stopped..."
                    sleep 5
                    '''
                }
            }
        }

        stage('Checkout SCM') {
            steps {
                // Immer Jenkinsfile von Main holen
                checkout([$class: 'GitSCM',
                    branches: [[name: 'main']],
                    userRemoteConfigs: [[url: 'https://github.com/Nadolze/MediTrack.git', credentialsId: '727a0953-0a0a-49a6-b1ff-e85298405d1c']]
                ])
            }
        }

        stage('Setup Environment') {
            steps {
                script {
                    def branch = env.BRANCH_NAME
                    def deployDir = "${BASE_DEPLOY_DIR}/${branch}"

                    def serverPort = 9093  // Default für unbekannte Branches
                    if(branch == "main") serverPort = 9090
                    else if(branch == "test") serverPort = 9091
                    else if(branch == "features") serverPort = 9092

                    echo "Branch: ${branch}"
                    echo "Deploy dir: ${deployDir}"
                    echo "Server Port: ${serverPort}"
                    echo "Maven Home: ${MAVEN_HOME}"

                    sh "mkdir -p ${deployDir}"

                    // Environment-Variablen für Deploy
                    env.DEPLOY_DIR = deployDir
                    env.SERVER_PORT = serverPort
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    echo "🔨 Building JAR..."
                    sh "${MAVEN_HOME}/bin/mvn clean package -DskipTests"
                }
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
                    ExecStart=/usr/bin/java -jar ${env.DEPLOY_DIR}/meditrack-0.0.1-SNAPSHOT.jar --server.port=${env.SERVER_PORT}
                    Restart=always

                    [Install]
                    WantedBy=multi-user.target
                    EOF

                    sudo systemctl daemon-reload
                    sudo systemctl enable meditrack-${env.BRANCH_NAME}.service
                    sudo systemctl restart meditrack-${env.BRANCH_NAME}.service
                    """
                }
            }
        }

        stage('Show Running') {
            steps {
                sh "ps -ef | grep meditrack || true"
                sh "netstat -tulpn | grep java || true"
            }
        }
    }
}
