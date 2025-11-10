pipeline {
    agent any

    environment {
        APP_NAME = "meditrack"
        JAR_NAME = "meditrack-0.0.1-SNAPSHOT.jar"
        DEPLOY_BASE = "/opt/meditrack"
        MAVEN_HOME = tool name: 'Maven_3.9.11', type: 'maven'
    }

    stages {
        stage('Stop old instances (safe)') {
            steps {
                echo "🔧 Versuche alte Instanzen zu stoppen (falls vorhanden)..."
                script {
                    if (isUnix()) {
                        sh '''
                            sudo pkill -f meditrack-0.0.1-SNAPSHOT.jar || true
                            sleep 2
                        '''
                    } else {
                        bat '''
                            taskkill /F /IM java.exe /FI "WINDOWTITLE eq meditrack*" || exit 0
                            timeout /t 2 >nul
                        '''
                    }
                }
            }
        }

        stage('Clean Workspace') {
            steps {
                echo "🧹 Lösche alten Workspace..."
                deleteDir()
            }
        }

        stage('Checkout') {
            steps {
                echo "📦 Hole Code aus Git..."
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo "🔧 Starte Maven Build..."
                script {
                    if (isUnix()) {
                        sh "${MAVEN_HOME}/bin/mvn clean package -DskipTests"
                    } else {
                        bat "\"%MAVEN_HOME%\\bin\\mvn.cmd\" clean package -DskipTests"
                    }
                }
                echo "✅ Maven Build fertig und JAR vorhanden."
            }
        }

        stage('Test') {
            steps {
                echo "🧪 Führe Tests aus..."
                script {
                    if (isUnix()) {
                        sh "${MAVEN_HOME}/bin/mvn test"
                    } else {
                        bat "\"%MAVEN_HOME%\\bin\\mvn.cmd\" test"
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                echo "🚀 Starte Deployment..."
                script {
                    // Dynamische Port-Zuweisung je nach Branch
                    def branchPort = [
                        'main': 9090,
                        'dev': 9091,
                        'feature': 9092
                    ][env.BRANCH_NAME] ?: 9099

                    if (isUnix()) {
                        sh """
                            echo "🔧 Stoppe alte Instanz auf Port ${branchPort}..."
                            sudo pkill -f "${JAR_NAME}" || true
                            sleep 2

                            echo "🚚 Kopiere neue Version..."
                            sudo mkdir -p ${DEPLOY_BASE}/${env.BRANCH_NAME}
                            sudo cp target/${JAR_NAME} ${DEPLOY_BASE}/${env.BRANCH_NAME}/

                            echo "🚀 Starte neue Instanz..."
                            nohup java -Xmx256m -jar ${DEPLOY_BASE}/${env.BRANCH_NAME}/${JAR_NAME} --server.port=${branchPort} > ${DEPLOY_BASE}/${env.BRANCH_NAME}/app.log 2>&1 &
                            echo "✅ Deployment abgeschlossen (läuft auf Port ${branchPort})."
                        """
                    } else {
                        bat """
                            echo Stoppe alte Instanz...
                            taskkill /F /IM java.exe /FI "WINDOWTITLE eq meditrack*" || exit 0
                            timeout /t 2 >nul

                            echo Starte neue Instanz...
                            start "meditrack-${env.BRANCH_NAME}" java -Xmx256m -jar target\\${JAR_NAME} --server.port=${branchPort}
                            echo Deployment abgeschlossen (läuft auf Port ${branchPort})
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo "🎉 Build & Deployment erfolgreich!"
        }
        failure {
            echo "❌ Build oder Deployment fehlgeschlagen!"
        }
        always {
            echo "🧾 Pipeline abgeschlossen (${env.BRANCH_NAME})"
        }
    }
}
