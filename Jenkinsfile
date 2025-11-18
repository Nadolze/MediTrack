pipeline {
    agent any

    environment {
        MAIN_BRANCH = "main"
        TEST_BRANCH = "test"
        FEATURE_BRANCH = "features"
        MAVEN = "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11/bin/mvn"
    }

    stages {

        stage('Checkout Code') {
            steps {
                checkout scm
                script {
                    BRANCH = env.BRANCH_NAME
                    echo "Detected branch: ${BRANCH}"
                }
            }
        }

        stage('Determine Port') {
            steps {
                script {
                    if (BRANCH == MAIN_BRANCH) {
                        PORT = 9090
                    }
                    else if (BRANCH == TEST_BRANCH) {
                        PORT = 9091
                    }
                    else if (BRANCH == FEATURE_BRANCH) {
                        PORT = 9092
                    }
                    else {
                        // other branches → assign dynamically
                        PORT = 9093 + (BRANCH.hashCode().abs() % 10)
                    }

                    echo "▶ Assigned port: ${PORT}"
                }
            }
        }

        stage('Build') {
            steps {
                sh """
                    ${MAVEN} clean package -DskipTests
                """
            }
        }

        stage('Stop Old Instance') {
            steps {
                sh """
                    pkill -f "meditrack.*--server.port=${PORT}" || true
                """
            }
        }

        stage('Run New Instance') {
            steps {
                sh """
                    nohup java -jar target/meditrack-0.0.1-SNAPSHOT.jar --server.port=${PORT} >/dev/null 2>&1 &
                    sleep 2
                """
                echo "✔ Server started on port ${PORT}"
            }
        }
    }

    post {
        failure {
            echo "❌ Build or deploy failed"
        }
        success {
            echo "✔ Done: Branch ${BRANCH} is running on port ${PORT}"
        }
    }
}
