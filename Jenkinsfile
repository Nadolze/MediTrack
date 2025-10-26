pipeline {
    agent any

    environment {
        DEFAULT_PORT = '9090'
        BACKUP_PORT = '8080'
    }

    stages {
        stage('Build') {
            steps {
                script {
                    echo "🔧 Starte Build-Prozess..."

                    // Automatische OS-Erkennung
                    def mvnCmd = isUnix() ? 'mvn' : 'mvn.cmd'

                    // Maven Build
                    try {
                        if (isUnix()) {
                            sh "${mvnCmd} clean package -DskipTests"
                        } else {
                            bat "${mvnCmd} clean package -DskipTests"
                        }
                        echo "✅ Build erfolgreich abgeschlossen."
                    } catch (err) {
                        error "❌ Maven-Build fehlgeschlagen. Bitte prüfe Maven-Installation oder Pfad!"
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    echo "🧪 Führe automatisierte Tests aus..."
                    def mvnCmd = isUnix() ? 'mvn' : 'mvn.cmd'
                    try {
                        if (isUnix()) {
                            sh "${mvnCmd} test"
                        } else {
                            bat "${mvnCmd} test"
                        }
                        echo "✅ Tests erfolgreich bestanden."
                    } catch (err) {
                        error "❌ Tests fehlgeschlagen."
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    echo "🚀 Starte Deployment..."

                    // Port-Erkennung mit Fallback
                    def port = env.DEFAULT_PORT
                    def isPortFree = true

                    try {
                        new java.net.Socket("localhost", port.toInteger()).close()
                        isPortFree = false
                    } catch (Exception ignore) {}

                    if (!isPortFree) {
                        echo "⚠️ Port ${port} ist belegt – weiche auf ${BACKUP_PORT} aus."
                        port = BACKUP_PORT
                    }

                    echo "➡️ Deployment auf Port ${port}"

                    if (isUnix()) {
                        sh """
                            mkdir -p ~/meditrack
                            cp target/mediweb-0.0.1-SNAPSHOT.jar ~/meditrack/
                            pkill -f mediweb || true
                            nohup java -jar ~/meditrack/mediweb-0.0.1-SNAPSHOT.jar --server.port=${port} > ~/meditrack/log.txt 2>&1 &
                        """
                    } else {
                        bat """
                            if not exist C:\\meditrack mkdir C:\\meditrack
                            copy target\\mediweb-0.0.1-SNAPSHOT.jar C:\\meditrack\\ /Y
                            powershell -Command "Stop-Process -Name java -ErrorAction SilentlyContinue"
                            start /B java -jar C:\\meditrack\\mediweb-0.0.1-SNAPSHOT.jar --server.port=${port}
                        """
                    }

                    echo "✅ MediTrack erfolgreich gestartet."
                }
            }
        }

        stage('Health Check') {
            steps {
                script {
                    echo "🔍 Überprüfe Erreichbarkeit unter http://localhost:${DEFAULT_PORT} oder ${BACKUP_PORT} ..."
                    def urls = ["http://localhost:${DEFAULT_PORT}", "http://localhost:${BACKUP_PORT}"]
                    def reachable = false

                    urls.each { u ->
                        try {
                            def conn = new URL(u).openConnection()
                            conn.connectTimeout = 5000
                            conn.readTimeout = 5000
                            conn.inputStream.text
                            echo "✅ Server antwortet erfolgreich auf ${u}"
                            reachable = true
                        } catch (Exception e) {
                            echo "⚠️ Keine Antwort auf ${u}"
                        }
                    }

                    if (!reachable) {
                        error "❌ Health Check fehlgeschlagen – keine Instanz erreichbar."
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
    }
}
