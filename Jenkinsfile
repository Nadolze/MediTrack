pipeline {
    agent any
    environment {
        APP_NAME = "MediTrack"
        MAVEN_HOME = tool 'Maven 3.9.11'
        JAR_NAME = "meditrack-0.0.1-SNAPSHOT.jar"
        DEPLOY_DIR_UNIX = "/opt/meditrack/main"
        DEPLOY_DIR_WIN = "C:\\\\meditrack\\\\main"

    }

    stages {
        stage('Stop old instances (safe)') {
            steps {
                script {
                    echo "🔧 Versuche alte Instanzen zu stoppen (falls vorhanden)..."
                    if (isUnix()) {
                        // Beende alte Prozesse, ohne Pipeline zu failen
                        sh "pkill -f '${JAR_NAME}' || true"
                    } else {
                        bat """
                        powershell -NoProfile -Command "Get-WmiObject Win32_Process | Where-Object { \$_.CommandLine -match '${JAR_NAME}' } | ForEach-Object { Stop-Process -Id \$_.ProcessId -Force -ErrorAction SilentlyContinue }"
                        """
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
                script {
                    echo "🔧 Starte Maven Build..."
                    def mvnCmd = isUnix() ? "${MAVEN_HOME}/bin/mvn" : "\"${MAVEN_HOME}\\bin\\mvn.cmd\""
                    if (isUnix()) {
                        sh "${mvnCmd} clean package -DskipTests"
                    } else {
                        bat "${mvnCmd} clean package -DskipTests"
                    }

                    // Sicherstellen, dass das JAR erzeugt wurde
                    if (isUnix()) {
                        sh "test -f target/${JAR_NAME} || (echo '❌ JAR nicht gefunden: target/${JAR_NAME}' && exit 1)"
                    } else {
                        bat "if not exist target\\${JAR_NAME} (echo JAR nicht gefunden: target\\\\${JAR_NAME} & exit 1)"
                    }
                    echo "✅ Maven Build fertig und JAR vorhanden."
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    echo "🧪 Führe Tests aus..."
                    def mvnCmd = isUnix() ? "${MAVEN_HOME}/bin/mvn" : "\"${MAVEN_HOME}\\bin\\mvn.cmd\""
                    if (isUnix()) {
                        sh "${mvnCmd} test"
                    } else {
                        bat "${mvnCmd} test"
                    }
                    echo "✅ Tests abgeschlossen."
                }
            }
        }

        stage('Deploy') {
                    steps {
                        echo "🚀 Starte Deployment..."
                        script {
                            // Port pro Branch (z. B. main=9090, dev=9091, feature=9092)
                            def branchPort = [
                                'main': 9090,
                                'dev': 9091,
                                'staging': 9092
                            ][env.BRANCH_NAME] ?: 9099

                            echo "🌐 Branch ${env.BRANCH_NAME} → Port ${branchPort}"

                            if (isUnix()) {
                                sh """
                                set -e
                                sudo mkdir -p ${DEPLOY_BASE}/${env.BRANCH_NAME}

                                echo "🔧 Stoppe alte Instanz (falls vorhanden)..."
                                sudo pkill -f "${JAR_NAME}" || true

                                echo "📦 Kopiere neue Version..."
                                sudo cp target/${JAR_NAME} ${DEPLOY_BASE}/${env.BRANCH_NAME}/

                                echo "🚀 Starte neue Instanz..."
                                nohup java -Xmx256m -jar ${DEPLOY_BASE}/${env.BRANCH_NAME}/${JAR_NAME} --server.port=${branchPort} > ${DEPLOY_BASE}/${env.BRANCH_NAME}/app.log 2>&1 &
                                """
                            } else {
                                bat """
                                echo Stoppe alte Instanz...
                                for /f "tokens=5" %%p in ('netstat -aon ^| find "9090" ^| find "LISTENING"') do taskkill /PID %%p /F >nul 2>&1

                                echo Kopiere neue Version...
                                if not exist "%DEPLOY_BASE%\\${env.BRANCH_NAME}" mkdir "%DEPLOY_BASE%\\${env.BRANCH_NAME}"
                                copy target\\${JAR_NAME} "%DEPLOY_BASE%\\${env.BRANCH_NAME}\\${JAR_NAME}" /Y

                                echo Starte neue Instanz...
                                start /b java -Xmx256m -jar "%DEPLOY_BASE%\\${env.BRANCH_NAME}\\${JAR_NAME}" --server.port=${branchPort}
                                """
                            }

                            echo "✅ Deployment abgeschlossen. App läuft auf Port ${branchPort}"
                        }
                    }
                }
            }

        stage('Health Check') {
            steps {
                script {
                    def port = env.ACTIVE_PORT ?: PORT
                    echo "🔍 Prüfe MediTrack auf http://localhost:${port} (Health-Check)..."

                    def healthy = false
                    for (int i = 1; i <= 8; i++) {
                        echo "⏳ Versuch ${i}..."
                        sleep time: 4, unit: 'SECONDS'

                        try {
                            def code = ""
                            if (isUnix()) {
                                code = sh(script: "curl -s -o /dev/null -w '%{http_code}' http://localhost:${port} || echo 000", returnStdout: true).trim()
                            } else {
                                code = bat(script: "powershell -Command \"(Invoke-WebRequest -Uri http://localhost:${port} -UseBasicParsing -ErrorAction SilentlyContinue).StatusCode\" || echo 000", returnStdout: true).trim()
                            }
                            echo "ℹ️ HTTP Status: ${code}"
                            if (code.contains("200")) {
                                healthy = true
                                break
                            }
                        } catch (err) {
                            echo "⚠️ Keine Antwort, warte kurz..."
                        }
                    }

                    if (!healthy) {
                        error "❌ Health Check fehlgeschlagen – MediTrack antwortet nicht auf Port ${port}"
                    } else {
                        echo "✅ Health Check erfolgreich — MediTrack läuft auf Port ${port}"
                    }
                }
            }
        }
    }

    post {
        success {
            echo "🎉 Build+Deploy erfolgreich. MediTrack erreichbar auf Port ${PORT}."
        }
        failure {
            echo "❌ Pipeline fehlgeschlagen."
        }
        always {
            echo "🏁 Pipeline Ende."
        }
    }
}
