pipeline {
	agent any
	environment {
		APP_NAME = "MediTrack"
		MAVEN_HOME = tool 'Maven 3.9.11'
	}

	stages {

		stage('Checkout') {
			steps {
				echo "📦 Hole Code aus Git..."
				checkout scm
			}
		}

		stage('Build') {
			steps {
				script {
					echo "🔧 Starte Build-Prozess..."
					def mvnCmd = isUnix() ? "${MAVEN_HOME}/bin/mvn" : "\"${MAVEN_HOME}\\bin\\mvn.cmd\""
					if (isUnix()) {
						sh "${mvnCmd} clean package -DskipTests"
					} else {
						bat "${mvnCmd} clean package -DskipTests"
					}
					echo "✅ Build erfolgreich."
				}
			}
		}

		stage('Test') {
			steps {
				script {
					echo "🧪 Führe Tests aus..."
					def mvnCmd = isUnix() ? "${MAVEN_HOME}/bin/mvn" : "\"${MAVEN_HOME}\\bin\\mvn.cmd\""
					if (isUnix()) {
						sh "${mvnCmd} test"
					} else {
						bat "${mvnCmd} test"
					}
					echo "✅ Tests erfolgreich abgeschlossen."
				}
			}
		}

		stage('Deploy') {
			steps {
				script {
					echo "🚀 Deployment wird vorbereitet..."

					// Benutzername ermitteln
					def currentUser = ""
					if (isUnix()) {
						currentUser = sh(script: "whoami", returnStdout: true).trim()
					} else {
						currentUser = bat(script: "echo %USERNAME%", returnStdout: true).trim()
					}

					// Port-Zuordnung
					def port = (currentUser.toLowerCase().contains("micro") || currentUser.toLowerCase().contains("wolfdeleu")) ? "9090" : "8080"
					echo "👤 Benutzer '${currentUser}' erkannt – ${APP_NAME} läuft auf Port ${port}"

					if (isUnix()) {
						// Linux/macOS
						sh """
                        mkdir -p /opt/meditrack/main
                        cp target/meditrack-0.0.1-SNAPSHOT.jar /opt/meditrack/main/
                        pkill -f meditrack-0.0.1-SNAPSHOT.jar || true
                        nohup java -jar /opt/meditrack/main/meditrack-0.0.1-SNAPSHOT.jar --server.port=${port} > /opt/meditrack/main/app.log 2>&1 &
                        echo "🚀 ${APP_NAME} gestartet (Port ${port})"
                        """
					} else {
						// Windows (läuft unabhängig vom Jenkins-Dienst)
						def deployDir = "C:\\\\meditrack\\\\main"
						bat """
                        if not exist "${deployDir}" mkdir "${deployDir}"
                        copy target\\meditrack-0.0.1-SNAPSHOT.jar "${deployDir}" /Y

                        :: Alte Instanzen beenden
                        powershell -NoProfile -Command "Get-WmiObject Win32_Process | Where-Object { \$_.CommandLine -match 'meditrack-0.0.1-SNAPSHOT.jar' } | ForEach-Object { Stop-Process -Id \$_.ProcessId -Force -ErrorAction SilentlyContinue }"

                        cd /d "${deployDir}"

                        :: Starte MediTrack entkoppelt vom Jenkins-Dienst
                        powershell -NoProfile -Command "& { (New-Object -ComObject WScript.Shell).Run('java -jar meditrack-0.0.1-SNAPSHOT.jar --server.port=${port}', 0, \$false) }"

                        echo "🚀 ${APP_NAME} gestartet (Port ${port})"
                        """
					}

					env.ACTIVE_PORT = port
				}
			}
		}

		stage('Health Check') {
			steps {
				script {
					def port = env.ACTIVE_PORT ?: "9090"
					echo "🔍 Überprüfe Erreichbarkeit auf http://localhost:${port}"

					def healthy = false
					for (int i = 1; i <= 6; i++) {
						echo "⏳ Versuch ${i}..."
						sleep time: 5, unit: 'SECONDS'

						try {
							def response = ""
							def content = ""
							if (isUnix()) {
								response = sh(script: "curl -s -o /dev/null -w '%{http_code}' http://localhost:${port}", returnStdout: true).trim()
								content = sh(script: "curl -s http://localhost:${port} | grep MediTrack || true", returnStdout: true).trim()
							} else {
								response = bat(script: "powershell -Command \"(Invoke-WebRequest -Uri http://localhost:${port} -UseBasicParsing).StatusCode\"", returnStdout: true).trim()
								content = bat(script: "powershell -Command \"(Invoke-WebRequest -Uri http://localhost:${port} -UseBasicParsing).Content | Select-String -Pattern 'MediTrack' | Select -First 1\"", returnStdout: true).trim()
							}

							response = response.tokenize('\n').last().trim()
							echo "ℹ️ HTTP Status: ${response}"
							echo "🔎 Gefundener Inhalt: ${content}"

							if ((response.contains("200") || response.contains("302")) && content.contains("MediTrack")) {
								healthy = true
								break
							}
						} catch (err) {
							echo "⚠️ Keine Antwort erhalten, erneuter Versuch..."
						}
					}

					if (!healthy) {
						error "❌ Health Check fehlgeschlagen – ${APP_NAME} antwortet nicht auf Port ${port}"
					} else {
						echo "✅ ${APP_NAME} läuft stabil auf Port ${port}."
						echo "🔗 Öffne: http://localhost:${port}"
					}
				}
			}
		}
	}

	post {
		success {
			echo "🎉 Build, Test und Deployment erfolgreich abgeschlossen."
			echo "WIN Powershell start mit: \"java -jar meditrack-0.0.1-SNAPSHOT.jar --server.port=9090\" oder 8080"
		}
		failure {
			echo "❌ Build oder Deployment fehlgeschlagen."
		}
		always {
			echo "🏁 Pipeline abgeschlossen."
		}
	}
}
