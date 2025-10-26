pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                script {
                    // Plattformübergreifender Maven-Build
                    if (isUnix()) {
                        sh 'mvn clean package'
                    } else {
                        bat 'mvn clean package'
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Branchname aus Jenkins-Umgebung
                    def branch = env.BRANCH_NAME ?: "main"

                    // Port dynamisch bestimmen: main=8080, sonst 808X
                    def port = (branch == 'main') ? '8080' :
                               (8080 + Math.abs(branch.hashCode() % 100)).toString()

                    echo "Deploying branch ${branch} on port ${port}"

                    if (isUnix()) {
                        // Zielpfad dynamisch bestimmen (Linux)
                        def targetDir = "/opt/meditrack/${branch}"
                        sh """
                            sudo mkdir -p ${targetDir}
                            sudo cp target/mediweb-0.0.1-SNAPSHOT.jar ${targetDir}/
                            sudo systemctl stop meditrack-${branch} || true
                        """

                        // Dynamische systemd-Service-Datei erzeugen
                        sh """
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
                        // Zielpfad dynamisch bestimmen (Windows)
                        def targetDir = "C:\\\\meditrack\\\\${branch}"
                        bat """
                            if not exist "${targetDir}" mkdir "${targetDir}"
                            copy target\\mediweb-0.0.1-SNAPSHOT.jar "${targetDir}\\"
                        """

                        // Windows-Service simulieren (kein systemd)
                        // Hier könnte z. B. NSSM genutzt werden, wenn dauerhaft gewünscht
                        echo "Starte MediTrack-Service (Simulation unter Windows)"
                        bat """
                            echo Starting MediTrack ${branch} on port ${port}
                            rem Beispiel: java -jar %targetDir%\\mediweb-0.0.1-SNAPSHOT.jar --server.port=%port%
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo '✅ Build und Deployment erfolgreich abgeschlossen.'
        }
        failure {
            echo '❌ Build oder Deployment fehlgeschlagen.'
        }
    }
}
