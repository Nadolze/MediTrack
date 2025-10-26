pipeline {
    agent any

    tools {
        maven 'Maven 3.9.11'
    }

    stages {
        stage('Build') {
            steps {
                script {
                    echo "🔧 Starte Build-Prozess..."
                    if (isUnix()) {
                        sh 'mvn clean package -DskipTests'
                    } else {
                        bat 'mvn clean package -DskipTests'
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    echo "🧪 Führe automatisierte Tests aus..."
                    if (isUnix()) {
                        sh 'mvn test'
                    } else {
                        bat 'mvn test'
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Branchname aus Jenkins-Umgebung
                    def branch = env.BRANCH_NAME ?: "main"

                    // Priorität: Port 9090, wenn belegt -> auf 8080 ausweichen
                    def port = 9090
                    try {
                        new Socket("localhost", 9090).close()
                        port = 8080
                        echo "⚠️ Port 9090 ist belegt, wechsle auf Port 8080."
                    } catch (Exception e) {
                        echo "✅ Port 9090 ist frei."
                    }

                    echo "🚀 Deploying branch ${branch} on port ${port}"

                    // Zielverzeichnis für JARs
                    def targetDir = isUnix() ? "/opt/meditrack/${branch}" : "C:\\meditrack\\${branch}"
                    if (isUnix()) {
                        sh """
                            mkdir -p ${targetDir}
                            cp target/mediweb-0.0.1-SNAPSHOT.jar ${targetDir}/
                            sudo systemctl stop meditrack-${branch} || true
                        """
                    } else {
                        bat """
                            if not exist ${targetDir} mkdir ${targetDir}
                            copy target\\mediweb-0.0.1-SNAPSHOT.jar ${targetDir}\\ /Y
                            echo Stoppe vorherige MediTrack-Instanz (falls aktiv)...
                            powershell -Command "Stop-Process -Name java -ErrorAction SilentlyContinue"
                            echo Starte MediTrack auf Port ${port}...
                            start /B java -jar ${targetDir}\\mediweb-0.0.1-SNAPSHOT.jar --server.port=${port}
                        """
                    }
                }
            }
        }

        stage('Health Check') {
            steps {
                script {
                    echo "🔍 Überprüfe, ob die Anwendung unter http://localhost:9090 oder :8080 läuft..."
                    def healthy = false

                    // mehrfach prüfen mit Pause
                    for (int i = 0; i < 5; i++) {
                        try {
                            def response = new URL("http://localhost:9090").getText()
                            if (response.contains("MediTrack")) {
                                healthy = true
                                break
                            }
                        } catch (Exception e) { sleep(5) }

                        try {
                            def response = new URL("http://localhost:8080").getText()
                            if (response.contains("MediTrack")) {
                                healthy = true
                                break
                            }
                        } catch (Exception e) { sleep(5) }
                    }

                    if (!healthy) {
                        error("❌ Health Check fehlgeschlagen – App auf keinem Port erreichbar.")
                    } else {
                        echo "✅ Anwendung erfolgreich erreichbar!"
                    }
                }
            }
        }
    }

    post {
        success {
            echo "🎉 Build und Deployment erfolgreich abgeschlossen!"
        }
        failure {
            echo "❌ Build oder Deployment fehlgeschlagen."
        }
    }
}
