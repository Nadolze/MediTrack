pipeline {
    agent any

    tools {
        maven 'Maven 3.9.11'
    }

    triggers {
        githubPush()
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
                    // wolfdeleu
                    def branch = env.BRANCH_NAME
                    def port = '9090'

                    // 🔄 Fallback auf 8080, falls 9090 bereits belegt
                    try {
                        def socket = new Socket("localhost", port.toInteger())
                        echo "⚠️ Port ${port} ist belegt – weiche auf 8080 aus."
                        port = '8080'
                        socket.close()
                    } catch (Exception e) {
                        echo "✅ Port ${port} ist frei."
                    }

                    echo "🚀 Deploying branch ${branch} on port ${port}"

                    def targetDir = isUnix() ? "/opt/meditrack/${branch}" : "C:\\meditrack\\${branch}"

                    if (isUnix()) {
                        sh """
                            sudo mkdir -p ${targetDir}
                            sudo cp target/mediweb-0.0.1-SNAPSHOT.jar ${targetDir}/
                            sudo systemctl stop meditrack-${branch} || true
                            sudo bash -c 'cat > /etc/systemd/system/meditrack-${branch}.service <<EOF
[Unit]
Description=MediTrack (${branch})
After=network.target

[Service]
User=jenkins
ExecStart=/usr/bin/java -jar ${targetDir}/mediweb-0.0.1-SNAPSHOT.jar --server.port=${port}
Restart=always

[Install]
WantedBy=multi-user.target
EOF'
                            sudo systemctl daemon-reload
                            sudo systemctl start meditrack-${branch}
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
                    def ports = ['9090', '8080']
                    def success = false

                    for (p in ports) {
                        try {
                            def response = new URL("http://localhost:${p}").text
                            if (response.contains("Willkommen") || response.contains("Login")) {
                                echo "✅ Server antwortet erfolgreich auf Port ${p}!"
                                success = true
                                break
                            }
                        } catch (Exception e) {
                            echo "⚠️ Keine Antwort auf Port ${p}."
                        }
                    }

                    if (!success) {
                        error "❌ Health Check fehlgeschlagen – App auf keinem Port erreichbar."
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
