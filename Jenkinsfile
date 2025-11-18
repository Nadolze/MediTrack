pipeline {
    agent any
    options {
        disableConcurrentBuilds() // nur 1 Build gleichzeitig
        timeout(time: 30, unit: 'MINUTES') // max 30 Minuten pro Build
    }
    tools {
        maven 'Maven_3.9.11'
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: '*/', url: 'https://github.com/DEIN_USER/MediTrack.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        stage('Run Docker Container') {
            steps {
                script {
                    def branch = env.GIT_BRANCH.replaceAll('origin/', '')
                    def port = getPortForBranch(branch)

                    sh """
                    docker build -t meditrack:${branch} .
                    docker rm -f ${branch} || true
                    docker run -d --name ${branch} \
                        -p ${port}:${port} \
                        --cpus="1" --memory="500m" \
                        meditrack:${branch} \
                        --server.port=${port}
                    """
                }
            }
        }
    }
}

def getPortForBranch(branch) {
    if(branch == 'main') return 9090
    if(branch == 'test') return 9091
    if(branch == 'features') return 9092

    def dynamicPortStart = 9093
    def hash = branch.hashCode() % 100
    return dynamicPortStart + Math.abs(hash)
}
