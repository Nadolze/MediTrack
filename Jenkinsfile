pipeline {
    agent any
    environment {
        APP_NAME = "MediTrack"
        MAVEN_HOME = tool 'Maven 3.9.11'
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
                    echo "🚀 Deployment wird vorbereitet..."

                    // Deployment-Port (fest oder dynamisch)
                    def port = "9090"
                    echo "🔹 MediTrack wird auf Port ${port} gestartet"

                    if (isUnix()) {
                        // Linux / macOS
                        sh """
                        mkdir -p /opt/meditrack/main
                        rm -f /opt/meditrack/main/meditrack-*.jar
                        pkill -f 'meditrack-.*\\.jar' || true
                        cp target/meditrack-0.0.1-SNAPSHOT.jar /opt/meditrack/main/
                        nohup java -jar /opt/meditrack/main/meditrack-0.0.1-SNAPSHOT.jar --server.port=${port} > /opt/meditrack/main/app.log 2>&1 &
                        echo "🚀 MediTrack wurde auf Port ${port} gestartet (Linux detached)."
                        """
                    } else {
                        // Windows
                        def deployDir = "C:\\\\meditrack\\\\main"
                        bat """
                        if not exist "${deployDir}" mkdir "${deployDir}"
                        del /Q "${deployDir}\\\\meditrack-*.jar"
                        powershell -NoProfile -Command "Get-WmiObject Win32_Process | Where-Object { \$_.CommandLine -match 'meditrack-.*\\.jar' } | ForEach-Object { Stop-Process -Id \$_.ProcessId -Force -ErrorAction SilentlyContinue }"
                        copy target\\meditrack-0.0.1-SNAPSHOT.jar "${deployDir}" /Y
                        cd /d "${deployDir}"
                        powershell -NoProfile -Command "& { (New-Object -ComObject WScript.Shell).Run('java -jar meditrack-0.0.1-SNAPSHOT.jar --server.port=${port}', 0, \$false) }"
                        echo "🚀 MediTrack wurde via WMI-Detach gestartet (Port ${port})"
                        """
                    }

                    env.ACTIVE_PORT = port
                }
            }
        }

        stage('Health Check') {
            steps {
                script {
                    def port = env.ACTIVE_PORT ?: "9090"
                    echo "🔍 Prüfe MediTrack auf http://localhost:${port} ..."

                    def healthy = false
                    for (int i = 1; i <= 6; i++) {
                        echo "⏳ Versuch ${i}..."
                        sleep time: 5, unit: 'SECONDS'

                        try {
                            def response = ""
                            if (isUnix()) {
                                response = sh(script: "curl -s -o /dev/null -w '%{http_code}' http://localhost:${port}", returnStdout: true).trim()
                            } else {
                                response = bat(script: "powershell -Command \"(Invoke-WebRequest -Uri http://localhost:${port} -UseBasicParsing).StatusCode\"", returnStdout: true).trim()
                            }

                            echo "ℹ️ HTTP Status: ${response}"
                            if (response.contains("200")) {
                                healthy = true
                                break
                            }
                        } catch (err) {
                            echo "⚠️ Keine Antwort von MediTrack auf Port ${port}, warte kurz..."
                        }
                    }

                    if (!healthy) {
                        error "❌ Health Check fehlgeschlagen – MediTrack antwortet nicht auf Port ${port}"
                    } else {
                        echo "✅ MediTrack läuft stabil auf Port ${port}."
                    }
                }
            }
        }

    }

    post {
        success {
            echo "🎉 Build, Test und Deployment erfolgreich abgeschlossen."
        }
        failure {
            echo "❌ Build oder Deployment fehlgeschlagen."
        }
        always {
            echo "🏁 Pipeline abgeschlossen."
        }
    }
}
