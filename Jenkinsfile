pipeline {
    agent any

    environment {
        APP_NAME = "MediTrack"
        JAR_NAME = "mediweb-0.0.1-SNAPSHOT.jar"
        DEFAULT_PORT = "9090"
    }

    stages {
        stage('Tool Install') {
            steps {
                echo "🧰 Stelle sicher, dass Java & Maven verfügbar sind..."
                // Jenkins stellt Tool-Umgebung bereit, falls konfiguriert
                tool name: 'Maven', type: 'maven'
                tool name: 'JDK', type: 'jdk'
            }
        }

        stage('Build') {
            steps {
                script {
                    echo "🔧 Starte Build-Prozess..."
                    if (isUnix()) {
                        sh "mvn clean package -DskipTests"
                    } else {
                        bat "mvn clean package -DskipTests"
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    echo "🧪 Führe automatisierte Tests aus..."
                    if (isUnix()) {
                        sh "mvn test"
                    } else {
                        bat "mvn test"
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Branchname und Port bestimmen
                    def branch = env.GIT_BRANCH ?: 'main'
                    def port = env.DEFAULT_PORT

                    echo "✅ Port ${port} wird verwendet."
                    echo "🚀 Deploying branch ${branch} auf Port ${port}"

                    // Zielpfad abhängig vom Betriebssystem
                    def targetDir = isUnix() ? "/opt/meditrack/${branch}" : "C:\\meditrack\\${branch}"

                    // Zielordner anlegen
                    if (isUnix()) {
                        sh "mkdir -p ${targetDir}"
                        sh "cp target/${JAR_NAME} ${targetDir}/"
                    } else {
                        bat "if not exist ${targetDir} mkdir ${targetDir}"
                        bat "copy target\\${JAR_NAME} ${targetDir}\\ /Y"
                    }

                    // Vorherige Instanz stoppen
                    echo "🛑 Stoppe alte MediTrack-Instanz (falls aktiv)..."
                    if (isUnix()) {
                        sh "pkill -f ${JAR_NAME} || true"
                    } else {
                        bat "powershell -Command \"Stop-Process -Name java -ErrorAction SilentlyContinue\""
                    }

                    // Anwendung starten
                    echo "🟢 Starte ${APP_NAME} auf Port ${port}..."
                    if (isUnix()) {
                        sh "nohup java -jar ${targetDir}/${JAR_NAME} --server.port=${port} > ${targetDir}/mediweb.log 2>&1 &"
                        sh "sleep 10"
                    } else {
                        bat "start /B java -jar ${targetDir}\\${JAR_NAME} --server.port=${port}"
                        bat "timeout /T 10 >nul"
                    }
                }
            }
        }

        stage('Health Check') {
            steps {
                script {
                    echo "🔍 Überprüfe, ob die Anwendung unter http://localhost:9090 oder :8080 läuft..."
                    try {
                        def response = new URL("http://localhost:9090").getText()
                        echo "✅ Server antwortet erfolgreich auf Port 9090!"
                    } catch (Exception e) {
                        try {
                            def response = new URL("http://localhost:8080").getText()
                            echo "✅ Server antwortet erfolgreich auf Port 8080!"
                        } catch (Exception ex) {
                            error("❌ Health Check fehlgeschlagen – App auf keinem Port erreichbar.")
                        }
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
