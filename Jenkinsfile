pipeline {
    agent any

    environment {
        MAVEN_HOME = "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11"
        BASE_DEPLOY_DIR = "/var/lib/jenkins/meditrack"
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                script {
                    sh "${MAVEN_HOME}/bin/mvn clean package -DskipTests"
                }
            }
        }

        stage('Run Main Server') {
            when {
                branch 'main'
            }
            steps {
                script {
                    DEPLOY_DIR="${BASE_DEPLOY_DIR}/main"
                    sh "mkdir -p ${DEPLOY_DIR}"
                    sh "cp target/meditrack-*.jar ${DEPLOY_DIR}/"
                    sh """
                    sudo bash -c 'cat <<EOF > /etc/systemd/system/meditrack-main.service
[Unit]
Description=MediTrack Main Branch
After=network.target

[Service]
User=jenkins
ExecStart=/usr/bin/java -jar ${DEPLOY_DIR}/meditrack-0.0.1-SNAPSHOT.jar --server.port=9090
Restart=always

[Install]
WantedBy=multi-user.target
EOF'
                    """
                    sh "sudo systemctl daemon-reload"
                    sh "sudo systemctl enable meditrack-main.service"
                    sh "sudo systemctl restart meditrack-main.service"
                    echo "✅ Main branch running on port 9090"
                }
            }
        }

        stage('Run Test Server') {
            when {
                branch 'test'
            }
            steps {
                script {
                    DEPLOY_DIR="${BASE_DEPLOY_DIR}/test"
                    sh "mkdir -p ${DEPLOY_DIR}"
                    sh "cp target/meditrack-*.jar ${DEPLOY_DIR}/"
                    sh """
                    sudo bash -c 'cat <<EOF > /etc/systemd/system/meditrack-test.service
[Unit]
Description=MediTrack Test Branch
After=network.target

[Service]
User=jenkins
ExecStart=/usr/bin/java -jar ${DEPLOY_DIR}/meditrack-0.0.1-SNAPSHOT.jar --server.port=9091
Restart=always

[Install]
WantedBy=multi-user.target
EOF'
                    """
                    sh "sudo systemctl daemon-reload"
                    sh "sudo systemctl enable meditrack-test.service"
                    sh "sudo systemctl restart meditrack-test.service"
                    echo "✅ Test branch running on port 9091"
                }
            }
        }

        stage('Run Features Server') {
            when {
                branch 'features'
            }
            steps {
                script {
                    DEPLOY_DIR="${BASE_DEPLOY_DIR}/features"
                    sh "mkdir -p ${DEPLOY_DIR}"
                    sh "cp target/meditrack-*.jar ${DEPLOY_DIR}/"
                    sh """
                    sudo bash -c 'cat <<EOF > /etc/systemd/system/meditrack-features.service
[Unit]
Description=MediTrack Features Branch
After=network.target

[Service]
User=jenkins
ExecStart=/usr/bin/java -jar ${DEPLOY_DIR}/meditrack-0.0.1-SNAPSHOT.jar --server.port=9092
Restart=always

[Install]
WantedBy=multi-user.target
EOF'
                    """
                    sh "sudo systemctl daemon-reload"
                    sh "sudo systemctl enable meditrack-features.service"
                    sh "sudo systemctl restart meditrack-features.service"
                    echo "✅ Features branch running on port 9092"
                }
            }
        }
    }
}
