pipeline {
    agent any

    environment {
        MAVEN_HOME = tool name: 'Maven_3.9.11', type: 'maven'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Determine Port') {
            steps {
                script {
                    // Branch-Port Mapping
                    if (env.BRANCH_NAME == 'main') {
                        PORT = 9090
                    } else if (env.BRANCH_NAME == 'test') {
                        PORT = 9091
                    } else if (env.BRANCH_NAME.startsWith('feature')) {
                        PORT = 9092
                    } else {
                        PORT = 9093
                    }
                    echo "👉 Branch '${env.BRANCH_NAME}' wird auf Port ${PORT} laufen."
                }
            }
        }

        stage('Build') {
            steps {
                withEnv(["PATH+MAVEN=${MAVEN_HOME}/bin"]) {
                    sh 'echo Using Maven Version: && mvn -v'
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    sh "./deploy.sh ${PORT}"
                }
            }
        }

        stage('Post Actions') {
            steps {
                echo "Branch ${env.BRANCH_NAME} läuft auf Port ${PORT}"
            }
        }
    }
}
