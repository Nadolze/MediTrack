pipeline {
    agent any

    environment {
        MVN_HOME = tool name: 'Maven_3.9.11', type: 'maven'
    }

    stages {

        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Determine Port') {
            steps {
                script {
                    // Haupt-Branches festlegen
                    if (env.BRANCH_NAME == 'main') {
                        PORT = 9090
                    } else if (env.BRANCH_NAME == 'test') {
                        PORT = 9091
                    } else {
                        // feature/* oder andere Branches → berechne dynamisch
                        // einfache Hash-Funktion auf Branch-Namen
                        hash = env.BRANCH_NAME.hashCode().abs() % 100 + 9100
                        PORT = hash
                    }
                    echo "Branch '${env.BRANCH_NAME}' wird auf Port ${PORT} laufen."
                }
            }
        }

        stage('Build') {
            steps {
                withEnv(["PATH+MAVEN=${MVN_HOME}/bin"]) {
                    sh """
                        echo "Using Maven version:"
                        mvn -v
                        mvn clean package -DskipTests
                    """
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // deploy.sh aus dem Branch nutzen
                    sh "chmod +x deploy.sh"
                    sh "./deploy.sh ${PORT}"
                }
            }
        }
    }

    post {
        always {
            echo "Branch ${env.BRANCH_NAME} läuft auf Port ${PORT}"
        }
    }
}
