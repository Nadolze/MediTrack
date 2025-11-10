pipeline {
    agent any
    environment {
        // Statische Basiswerte
        BASE_LINUX_DIR = '/opt/meditrack'
        BASE_WIN_DIR   = 'C:\\MediTrack'
        BASE_PORT      = '9090'
        MAVEN_HOME    = '/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.9.11'
    }
    stages {
        stage('Prepare Environment') {
            steps {
                script {
                    // Branch-spezifisches Deploy-Verzeichnis
                    env.BRANCH_DEPLOY_DIR = isUnix() ? "${BASE_LINUX_DIR}/${env.BRANCH_NAME}" : "${BASE_WIN_DIR}\\${env.BRANCH_NAME}"

                    // Branch-Port-Zuweisung (Offset)
                    def portOffsetMap = [ 'main':0, 'develop':1 ]
                    def offset = portOffsetMap[env.BRANCH_NAME] ?: 2
                    env.SERVER_PORT = (BASE_PORT.toInteger() + offset).toString()

                    echo "Deploy Dir: ${env.BRANCH_DEPLOY_DIR}"
                    echo "Server Port: ${env.SERVER_PORT}"
                }
            }
        }

        stage('Stop Old Instances & Clean') {
            steps {
                script {
                    if (isUnix()) {
                        sh """
                            pkill -f "${env.BRANCH_DEPLOY_DIR}/meditrack-*.jar" || true
                            rm -rf ${env.BRANCH_DEPLOY_DIR}/*
                            mkdir -p ${env.BRANCH_DEPLOY_DIR}
                        """
                    } else {
                        bat """
                            taskkill /F /IM meditrack-*.jar || echo Kein Prozess
                            if exist "${env.BRANCH_DEPLOY_DIR}" rmdir /S /Q "${env.BRANCH_DEPLOY_DIR}"
                            mkdir "${env.BRANCH_DEPLOY_DIR}"
                        """
                    }
                }
            }
        }

        stage('Checkout Code') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: env.BRANCH_NAME]],
                          userRemoteConfigs: [[url: 'https://github.com/Nadolze/MediTrack.git',
                                               credentialsId: 'github-creds']]])
            }
        }

        stage('Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh "${MAVEN_HOME}/bin/mvn clean package -DskipTests"
                    } else {
                        bat "\"${MAVEN_HOME}\\bin\\mvn\" clean package -DskipTests"
                    }
                }
            }
        }

        stage('Deploy & Start Server') {
            steps {
                script {
                    def jarName = "meditrack-0.0.1-SNAPSHOT.jar"
                    if (isUnix()) {
                        sh """
                            cp target/${jarName} ${env.BRANCH_DEPLOY_DIR}/
                            nohup java -jar ${env.BRANCH_DEPLOY_DIR}/${jarName} --server.port=${env.SERVER_PORT} > ${env.BRANCH_DEPLOY_DIR}/app.log 2>&1 &
                        """
                    } else {
                        bat """
                            copy target\\${jarName} "${env.BRANCH_DEPLOY_DIR}\\"
                            start /B java -jar "${env.BRANCH_DEPLOY_DIR}\\${jarName}" --server.port=${env.SERVER_PORT} > "${env.BRANCH_DEPLOY_DIR}\\app.log" 2>&1
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ Build & Deployment erfolgreich: Branch ${env.BRANCH_NAME} auf Port ${env.SERVER_PORT}"
        }
        failure {
            echo "❌ Build oder Deployment fehlgeschlagen für Branch ${env.BRANCH_NAME}"
        }
    }
}
