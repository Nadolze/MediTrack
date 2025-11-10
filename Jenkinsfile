pipeline {
    agent any

    environment {
        DEPLOY_DIR = "/opt/meditrack/main"
        JAR_NAME = "meditrack-0.0.1-SNAPSHOT.jar"
        JAVA_OPTS = "-Dspring.devtools.restart.enabled=false"
        BASE_PORT = 9090
    }

    stages {
        stage('Checkout') {
            steps {
                echo "📦 Checkout Code"
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo "🔧 Maven Build"
                sh """
                    /var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11/bin/mvn clean package -DskipTests
                """
                sh "test -f target/${JAR_NAME}"
                echo "✅ Build fertig"
            }
        }

        stage('Determine Port') {
            steps {
                script {
                    // Main Branch immer Port 9090
                    if (env.BRANCH_NAME == 'main') {
                        env.SERVER_PORT = "${BASE_PORT}"
                    } else {
                        // Feature-Branches bekommen einen Port > 9090
                        def branchHash = Math.abs(env.BRANCH_NAME.hashCode() % 1000)
                        env.SERVER_PORT = "${BASE_PORT + branchHash}"
                    }
                    echo "ℹ️ Branch ${env.BRANCH_NAME} wird auf Port ${env.SERVER_PORT} deployed"
                }
            }
        }

        stage('Stop old instance') {
            steps {
                echo "🛑 Stoppe alte Instanz (falls vorhanden)"
                sh """
                    pkill -f "${JAR_NAME}.*--server.port=${SERVER_PORT}" || true
                    sleep 2
                """
            }
        }

        stage('Deploy') {
            steps {
                echo "🚀 Deploy auf Server"
                sh """
                    mkdir -p ${DEPLOY_DIR}
                    cp target/${JAR_NAME} ${DEPLOY_DIR}/
                    nohup java ${JAVA_OPTS} -jar ${DEPLOY_DIR}/${JAR_NAME} --server.port=${SERVER_PORT} > ${DEPLOY_DIR}/app-${SERVER_PORT}.log 2>&1 &
                    echo \$! > ${DEPLOY_DIR}/meditrack-${SERVER_PORT}.pid
                """
            }
        }

        stage('Verify') {
            steps {
                echo "🔍 Prüfe, ob Server auf Port ${SERVER_PORT} läuft"
                sh """
                    sleep 5
                    if ! nc -z localhost ${SERVER_PORT}; then
                        echo "❌ Server läuft nicht auf Port ${SERVER_PORT}!"
                        exit 1
                    fi
                    echo "✅ Server läuft auf Port ${SERVER_PORT}!"
                """
            }
        }
    }

    post {
        success {
            echo "🏁 Deployment erfolgreich für Branch ${env.BRANCH_NAME} auf Port ${SERVER_PORT}"
        }
        failure {
            echo "❌ Deployment fehlgeschlagen für Branch ${env.BRANCH_NAME}"
        }
    }
}
