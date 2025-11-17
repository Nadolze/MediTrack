pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                script {
                    def mvn = isUnix() 
                        ? "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11/bin/mvn"
                        : "C:\\Maven\\bin\\mvn.cmd"

                    sh "${mvn} clean package -DskipTests"    // Linux
                }
            }
        }

        stage('Run (only main)') {
            when {
                branch 'main'
            }
            steps {
                script {
                    echo "Starting main branch JAR on port 9090"

                    if (isUnix()) {
                        sh '''
                            pkill -f "meditrack" || true
                            nohup java -jar target/meditrack-*.jar --server.port=9090 &
                        '''
                    } else {
                        bat '''
                            taskkill /IM java.exe /F
                            start java -jar target\\meditrack-*.jar --server.port=9090
                        '''
                    }
                }
            }
        }
    }
}
