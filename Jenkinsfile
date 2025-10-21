pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Branchname aus Jenkins-Umgebung
                    def branch = env.BRANCH_NAME
                    // Port dynamisch bestimmen: main=8080, sonst 808X
                    def port = (branch == 'main') ? '8080' :
                               (8080 + Math.abs(branch.hashCode() % 100)).toString()
                    
                    echo "Deploying branch ${branch} on port ${port}"

                    // Zielpfad dynamisch bestimmen
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
                }
            }
        }
    }
}
