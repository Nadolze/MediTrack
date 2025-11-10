pipeline {
    agent any
    environment {
        APP_NAME = "MediTrack"
        MAVEN_HOME = tool 'Maven 3.9.11'
        JAR_NAME = "meditrack-0.0.1-SNAPSHOT.jar"
        DEPLOY_DIR_UNIX = "/opt/meditrack/main"
        DEPLOY_DIR_WIN = "C:\\\\meditrack\\\\main"
        PORT = "9090"
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

        stage('Deploy (detached)') {
            steps {
                script {
                    echo "🚀 Deployment: kopieren, alte JAR entfernen, detached starten (Port ${PORT})"

                    if (isUnix()) {
                        sh """
                        set -e
                        mkdir -p ${DEPLOY_DIR_UNIX}
                        # Entferne alte JARs und alte Dateien
                        rm -f ${DEPLOY_DIR_UNIX}/meditrack-*.jar || true
                        # Stoppe eventuell laufende Instanzen nochmals
                        pkill -f '${JAR_NAME}' || true
                        # Kopiere neues JAR
                        cp target/${JAR_NAME} ${DEPLOY_DIR_UNIX}/
                        chmod 755 ${DEPLOY_DIR_UNIX}/${JAR_NAME} || true
                        # Starte detached (nohup) — losgelöst vom Jenkins-Job
                        nohup java -jar ${DEPLOY_DIR_UNIX}/${JAR_NAME} --server.port=${PORT} > ${DEPLOY_DIR_UNIX}/app.log 2>&1 &
                        echo "✅ Neuer Prozess gestartet (detached)."
                        """
                    } else {
                        // Windows: Deploy + WMI/COM-Detach
                        bat """
                        if not exist "${DEPLOY_DIR_WIN}" mkdir "${DEPLOY_DIR_WIN}"
                        del /Q "${DEPLOY_DIR_WIN}\\\\meditrack-*.jar" || echo "Keine alten JARs"
                        powershell -NoProfile -Command "Get-WmiObject Win32_Process | Where-Object { \$_.CommandLine -match '${JAR_NAME}' } | ForEach-Object { Stop-Process -Id \$_.ProcessId -Force -ErrorAction SilentlyContinue }"
                        copy /Y target\\${JAR_NAME} "${DEPLOY_DIR_WIN}"
                        cd /d "${DEPLOY_DIR_WIN}"
                        powershell -NoProfile -Command "& { (New-Object -ComObject WScript.Shell).Run('java -jar ${JAR_NAME} --server.port=${PORT}', 0, \$false) }"
                        echo "✅ Neuer Windows-Prozess gestartet (detached)."
                        """
                    }

                    // setze ACTIVE_PORT für Health-Check
                    env.ACTIVE_PORT = PORT
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
