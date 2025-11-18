pipeline {
    agent any

    environment {
        MAIN_BRANCH = "main"
        TEST_BRANCH = "test"
        FEATURE_BRANCH = "features"
    }

    stages {

        stage('Checkout Jenkinsfile from main') {
            steps {
                script {
                    sh """
                        git fetch origin ${MAIN_BRANCH}
                        git checkout origin/${MAIN_BRANCH} -- Jenkinsfile
                    """
                }
            }
        }

        stage('Checkout Source') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: "*/${env.GIT_BRANCH}"]],
                    userRemoteConfigs: [[
                        url: "https://github.com/Nadolze/MediTrack.git"
                    ]]
                ])
            }
        }

        stage('Select Port') {
            steps {
                script {

                    if (env.GIT_BRANCH == "origin/${MAIN_BRANCH}") {
                        PORT = 9090
                    }
                    else if (env.GIT_BRANCH == "origin/${TEST_BRANCH}") {
                        PORT = 9091
                    }
                    else if (env.GIT_BRANCH == "origin/${FEATURE_BRANCH}") {
                        PORT = 9092
                    }
                    else {
                        // hash fallback for unknown branches
                        PORT = 9093 + (env.GIT_BRANCH.hashCode().abs() % 10)
                    }

                    echo "Selected port: ${PORT}"
                }
            }
        }

        stage('Build') {
            steps {
                sh """
                    ./mvnw clean package -DskipTests
                """
            }
        }

        stage('Stop old version') {
            steps {
                sh """
                    pkill -f "meditrack.*--server.port=${PORT}" || true
                """
            }
        }

        stage('Run server') {
            steps {
                sh """
                    nohup java -jar target/meditrack-0.0.1-SNAPSHOT.jar --server.port=${PORT} &
                    sleep 3
                """
                echo "Server running at port: ${PORT}"
            }
        }
    }

    post {
        failure {
            echo "Build or deployment failed"
        }
        success {
            echo "✔ Deployment done"
        }
    }
}
