pipeline {
    agent any

    environment {
        MVN_HOME = tool name: 'Maven_3.9.11', type: 'maven'
        JAVA_HOME = tool name: 'JDK17', type: 'jdk'
        PATH = "${MVN_HOME}/bin:${JAVA_HOME}/bin:${env.PATH}"
        PORT = ''
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
                    if (env.BRANCH_NAME == 'main') {
                        env.PORT = '9090'
                    } else if (env.BRANCH_NAME == 'test') {
                        env.PORT = '9091'
                    } else if (env.BRANCH_NAME == 'features') {
                        env.PORT = '9092'
                    } else {
                        env.PORT = '9093'
                    }
                    echo "Branch '${env.BRANCH_NAME}' will run on port ${env.PORT}"
                }
            }
        }

        stage('Build') {
            steps {
                sh "mvn clean package -DskipTests"
            }
        }

        stage('Deploy') {
            steps {
                script {
                    echo "Deploying branch '${env.BRANCH_NAME}' on port ${env.PORT}..."

                    // Alte Instanz stoppen
                    sh """
                    PID=\$(lsof -ti:${env.PORT} || true)
                    if [ -n "\$PID" ]; then
                        echo "Stopping old process \$PID"
                        kill \$PID
                        sleep 5
                        if kill -0 \$PID 2>/dev/null; then
                            echo "Force killing \$PID"
                            kill -9 \$PID
                        fi
                    else
                        echo "No process running on port ${env.PORT}"
                    fi
                    """

                    // Neue Instanz starten
                    sh """
                    echo "Starting new instance on port ${env.PORT}..."
                    nohup java -jar target/meditrack-0.0.1-SNAPSHOT.jar --server.port=${env.PORT} > target/app_${env.PORT}.log 2>&1 &
                    sleep 3
                    echo "Instance started on port ${env.PORT}, tailing last 100 log lines:"
                    tail -n 100 target/app_${env.PORT}.log
                    """
                }
            }
        }
    }

    post {
        always {
            echo "Branch '${env.BRANCH_NAME}' is running on port ${env.PORT}"
        }
    }
}
