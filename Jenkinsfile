pipeline {
    agent any
    environment {
        APP_NAME = "MediTrack"
        PORT = "9090"
        MAVEN_HOME = tool 'Maven 3.9.11'
    }

    stages {

        stage('Checkout') {
            steps {
                echo "📦 Hole Code aus Git..."
                checkout scm
            }
        }

        stage('Tool Install') {
            steps {
                echo "🧰 Stelle sicher, dass Java & Maven verfügbar sind..."
                script {
                    def mvnCmd = isUnix() ? "${MAVEN_HOME}/bin/mvn" : "\"${MAVEN_HOME}\\bin\\mvn.cmd\""
                    echo "Verwendetes Maven: ${mvnCmd}"
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    echo "🔧 Starte Build-Prozess..."
                    def mvnCmd = isUnix() ? "${MAVEN_HOME}/bin/mvn" : "\"${MAVEN_HOME}\\bin\\mvn.cmd\""

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
                    def mvnCmd = isUnix() ? "${MAVEN_HOME}/bin/mvn" : "\"${MAVEN_HOME}\\bin\\mvn.cmd\""

                    if (isUnix()) {
                        sh "${mvnCmd} test"
                    } else {
                        bat "${mvnCmd} test"
                    }
                    echo "✅ Alle Tests erfolgreich bestanden."
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    echo "🚀 Starte Deployment für ${APP_NAME} auf Port ${PORT}..."

                    // Speicherpfad abhängig vom Betriebssystem
                    def deployDir = isUnix() ? "/opt/meditrack/${env.BRANCH_NAME ?: 'main'}" : "C:\\meditrack\\${env.BRANCH_NAME ?: 'main'}"

                    if (isUnix()) {
                        sh "mkdir -p ${deployDir}"
                        sh "cp target/mediweb-0.0.1-SNAPSHOT.jar ${deployDir}/"
                        sh "fuser -k ${PORT}/tcp || true"
                        sh "nohup java -jar ${deployDir}/mediweb-0.0.1-SNAPSHOT.jar --server.port=${PORT} > ${deployDir}/app.log 2>&1 &"
                    } else {
                        bat "if not exist ${deployDir} mkdir ${deployDir}"
                        bat "copy target\\mediweb-0.0.1-SNAPSHOT.jar ${deployDir}\\ /Y"
                        bat "powershell -Command \"Stop-Process -Name java -ErrorAction SilentlyContinue\""
                        bat "start /B java -jar ${deployDir}\\mediweb-0.0.1-SNAPSHOT.jar --server.port=${PORT}"
                    }

                    echo "✅ Deployment abgeschlossen – Anwendung sollte laufen."
                }
            }
        }

        stage('Health Check') {
            steps {
                script {
                    echo "🔍 Überprüfe, ob die Anwendung unter http://localhost:${PORT} läuft..."
                    sleep time: 5, unit: 'SECONDS'

                    try {
                        def response = isUnix() ?
                            sh(script: "curl -s -o /dev/null -w '%{http_code}' http://localhost:${PORT}", returnStdout: true).trim() :
                            bat(script: "powershell -Command \"(Invoke-WebRequest -Uri http://localhost:${PORT} -UseBasicParsing).StatusCode\"", returnStdout: true).trim()

                        if (response == '200') {
                            echo "✅ Server antwortet erfolgreich auf Port ${PORT}!"
                        } else {
                            error "⚠️ Health Check fehlgeschlagen – Antwort: ${response}"
                        }
                    } catch (err) {
                        error "❌ Health Check fehlgeschlagen – App scheint nicht erreichbar zu sein."
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
        always {
            echo "🏁 Pipeline abgeschlossen."
        }
    }
}
