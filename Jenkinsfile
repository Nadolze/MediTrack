pipeline {
    agent any

    environment {
        MAVEN_HOME = "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11"
        DEPLOY_BASE = "/var/lib/jenkins/meditrack"
        JAR_NAME = "meditrack-0.0.1-SNAPSHOT.jar"
    }

    stages {

        stage('Force Jenkinsfile from Main') {
            steps {
                script {
                    echo "🔄 Fetching Jenkinsfile from MAIN"
                    sh """
                        git fetch --all
                        MAIN_BRANCH=$(git remote show origin | sed -n '/HEAD branch/s/.*: //p')
                        git show origin/$MAIN_BRANCH:Jenkinsfile > Jenkinsfile
                    """
                }
            }
        }

        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Determine Branch and Port') {
            steps {
                script {
                    BRANCH = env.BRANCH_NAME

                    PORT = (BRANCH == "main") ? 9090 :
                           (BRANCH == "test") ? 9091 :
                           (BRANCH == "features") ? 9092 :
                           (9000 + (new Random().nextInt(99))) // fallback

                    DEPLOY_DIR = "${DEPLOY_BASE}/${BRANCH}"

                    echo "📌 Branch   : ${BRANCH}"
                    echo "📌 Port     : ${PORT}"
                    echo "📌 Deploy   : ${DEPLOY_DIR}"
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    sh """
                       ${MAVEN_HOME}/bin/mvn clean package -DskipTests
                    """
                }
            }
        }

        stage('Prepare Deployment') {
            steps {
                script {
                    echo "📦 Preparing deploy folder"
                    sh "mkdir -p ${DEPLOY_DIR}"
                    sh "cp target/${JAR_NAME} ${DEPLOY_DIR}/"
                }
            }
        }

        stage('Create Systemd Service') {
            steps {
                script {
                    echo "⚙️ Creating/updating service meditrack-${BRANCH}"

                    sh """
                    cat <<EOF | sudo tee /etc/systemd/system/meditrack-${BRANCH}.service
                    [Unit]
                    Description=MediTrack (${BRANCH}) on port ${PORT}
                    After=network.target

                    [Service]
                    User=jenkins
                    ExecStart=/usr/bin/java -jar ${DEPLOY_DIR}/${JAR_NAME} --server.port=${PORT}
                    Restart=always
                    WorkingDirectory=${DEPLOY_DIR}

                    [Install]
                    WantedBy=multi-user.target
                    EOF
                    """

                    sh """
                        sudo systemctl daemon-reload
                        sudo systemctl enable meditrack-${BRANCH}.service
                        sudo systemctl restart meditrack-${BRANCH}.service || sudo systemctl start meditrack-${BRANCH}.service
                    """

                    echo "🚀 Running: meditrack-${BRANCH}.service on port ${PORT}"
                }
            }
        }
    }
}
