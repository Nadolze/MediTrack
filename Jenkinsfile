pipeline {
    agent any
    environment {
        APP_NAME = "MediTrack"
        MAVEN_HOME = tool 'Maven 3.9.11'
        DEPLOY_DIR_LINUX = "/opt/meditrack/main"
        DEPLOY_DIR_WIN = "C:\\\\meditrack\\\\main"
        PORT = "9090"
    }

    stages {
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
                    echo "🔧 Starte Build-Prozess..."
                    def mvnCmd = isUnix() ? "${MAVEN_HOME}/bin/mvn" : "\"${MAVEN_HOME}\\bin\\mvn.cmd\""
                    if (isUnix()) {
                        sh "${mvnCmd} clean package -DskipTests"
                    } else {
                        bat "${mvnCmd} clean package -DskipTests"
                    }
                    echo "✅ Build erfolgreich."
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
                    echo "✅ Tests erfolgreich abgeschlossen."
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    echo "🚀 Deployment wird vorbereitet auf Port ${PORT}..."

                    if (isUnix()) {
                        // Linux Deployment
                        sh """
                        mkdir -p ${DEPLOY_DIR_LINUX}
                        cp target/meditrack-0.0.1-SNAPSHOT.jar ${DEPLOY_DIR_LINUX}/
                        pkill -f meditrack-0.0.1-SNAPSHOT.jar || true
                        nohup java -jar ${DEPLOY_DIR_LINUX}/meditrack-0.0.1-SNAPSHOT.jar --server.port=${PORT} > ${DEPLOY_DIR_LINUX}/app.log 2>&1 &
                        echo "🚀 MediTrack wurde auf Port ${PORT} gestartet (Linux detached)."
                        """
                    } else {
                        // Windows Deployment
                        bat """
                        if not exist "${DEPLOY_DIR_WIN}" mkdir "${DEPLOY_DIR_WIN}"
                        copy target\\meditrack-0.0.1-SNAPSHOT.jar "${DEPLOY_DIR_WIN}" /Y

                        :: Alte Instanzen stoppen
                        powershell -NoProfile -Command "Get-WmiObject Win32_Process | Where-Object { \$_.CommandLine -match 'meditrack-0.0.1-SNAPSHOT.jar' } | ForEach-Object { Stop-Process -Id \$_.ProcessId -Force -ErrorAction SilentlyContinue }"

                        cd /d "${DEPLOY_DIR_WIN}"

                        :: Starte MediTrack losgelöst vom Jenkins-Service
                        powershell -NoProfile -Command "& { (New-Object -ComObject WScript.Shell).Run('java -jar meditrack-0.0.1-SNAPSHOT.jar --server.port=${PORT}', 0, \$false) }"

                        echo "🚀 MediTrack wurde via WMI-Detach gestartet (Port ${PORT})"
                        """
                    }
                }
            }
        }

        stage('Health Check') {
            steps {
                script {
                    echo "🔍 Prüfe Erreichbarkeit auf http://localhost:${PORT}"
                    def healthy = false
                    for (int i = 1; i <= 6; i++) {
                        echo "⏳ Versuch ${i}..."
                        sleep time: 5, unit: 'SECONDS'

                        try {
                            def response = ""
                            if (isUnix()) {
                                response = sh(script: "curl -s -o /dev/null -w '%{http_code}' http://localhost:${PORT}", returnStdout: true).trim()
                            } else {
                                response = bat(script: "powershell -Command \"(Invoke-WebRequest -Uri http://localhost:${PORT} -UseBasicParsing).StatusCode\"", returnStdout: true).trim()
                            }

                            echo "ℹ️ HTTP Status: ${response}"
                            if (response.contains("200")) {
                                healthy = true
                                break
                            }
                        } catch (err) {
                            echo "⚠️ Keine Antwort erhalten, warte kurz..."
                        }
                    }

                    if (!healthy) {
                        error "❌ Health Check fehlgeschlagen – MediTrack antwortet nicht auf Port ${PORT}"
                    } else {
                        echo "✅ Anwendung läuft stabil auf Port ${PORT}."
                        echo "🔗 Öffne: http://localhost:${PORT}"
                    }
                }
            }
        }
    }

    post {
        success {
            echo "🎉 Build, Test und Deployment erfolgreich abgeschlossen."
            echo "💡 Windows Start: java -jar C:\\meditrack\\main\\meditrack-0.0.1-SNAPSHOT.jar --server.port=9090"
        }
        failure {
            echo "❌ Build oder Deployment fehlgeschlagen."
        }
        always {
            echo "🏁 Pipeline abgeschlossen."
        }
    }
}
