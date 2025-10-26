pipeline {
    agent any

    // Automatische Verwendung der in Jenkins konfigurierten Maven-Installation
    tools {
        maven 'Maven 3.9.11'
    }

    stages {
        stage('Build') {
            steps {
                script {
                    // Prüfen, ob Jenkins auf Windows oder Linux läuft
                    if (isUnix()) {
                        // Linux: Verwende 'sh'
                        sh 'mvn clean package'
                    } else {
                        // Windows: Verwende 'bat'
                        bat 'mvn clean package'
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Branchname aus Jenkins-Umgebung
                    def branch = env.BRANCH_NAME

                    // Fester Port für lokale Instanz (wolfdeleu)
                    def port = '9090'
                    echo "Deploying branch ${branch} on fixed port ${port}"

                    // Zielpfad dynamisch bestimmen – plattformabhängig
                    def targetDir
                    if (isUnix()) {
                        targetDir = "/opt/meditrack/${branch}"
                    } else {
                        targetDir = "C:\\meditrack\\${branch}"
                    }

                    // Deployment-Befehle je nach OS
                    if (isUnix()) {
                        // Linux: Deployment mit systemd
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
                        // Windows: Deployment mit PowerShell / CMD
                        bat """
                            if not exist ${targetDir} mkdir ${targetDir}
                            copy target\\mediweb-0.0.1-SNAPSHOT.jar ${targetDir}\\ /Y
                            echo Stopping existing MediTrack process (if running)...
                            powershell -Command "Stop-Process -Name java -ErrorAction SilentlyContinue"
                            echo Starting MediTrack on port ${port}...
                            start /B java -jar ${targetDir}\\mediweb-0.0.1-SNAPSHOT.jar --server.port=${port}
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ Build und Deployment erfolgreich abgeschlossen (Port 9090)."
        }
        failure {
            echo "❌ Build oder Deployment fehlgeschlagen."
        }
    }
}
