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

					try {
						if (isUnix()) {
							sh "${mvnCmd} clean package -DskipTests"
						} else {
							bat "${mvnCmd} clean package -DskipTests"
						}
						echo "✅ Build erfolgreich."
					} catch (err) {
						error "❌ Maven-Build fehlgeschlagen!"
					}
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

					def currentUser = ""
					if (!isUnix()) {
						currentUser = bat(script: 'echo %USERNAME%', returnStdout: true).trim()
					} else {
						currentUser = sh(script: 'whoami', returnStdout: true).trim()
					}

					def port = (currentUser.toLowerCase().contains("micro") || currentUser.toLowerCase().contains("wolfdeleu")) ? "9090" : "8080"
					echo "👤 Benutzer '${currentUser}' erkannt – MediTrack läuft auf Port ${port}"

					if (isUnix()) {
						sh """
                        mkdir -p /opt/meditrack/main
                        cp target/mediweb-0.0.1-SNAPSHOT.jar /opt/meditrack/main/
                        pkill -f mediweb-0.0.1-SNAPSHOT.jar || true
                        nohup java -jar /opt/meditrack/main/mediweb-0.0.1-SNAPSHOT.jar --server.port=${port} > /opt/meditrack/main/app.log 2>&1 &
                        """
					} else {
						def deployDir = "C:\\\\meditrack\\\\main"
						bat """
                        if not exist "${deployDir}" mkdir "${deployDir}"
                        copy target\\mediweb-0.0.1-SNAPSHOT.jar "${deployDir}" /Y

                        powershell -NoProfile -Command "Get-WmiObject Win32_Process | Where-Object { \$_.CommandLine -match 'mediweb-0.0.1-SNAPSHOT.jar' } | ForEach-Object { Stop-Process -Id \$_.ProcessId -Force -ErrorAction SilentlyContinue }"

                        cd /d "${deployDir}"
                        start /min cmd /c "java -jar mediweb-0.0.1-SNAPSHOT.jar --server.port=${port} > app.log 2>&1"
                        """
						echo "🚀 MediTrack gestartet (Port ${port})"
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
					for (int i = 1; i <= 5; i++) {
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
							echo "⚠️ Keine Antwort erhalten, versuche erneut..."
						}
					}

					if (!healthy) {
						error "❌ Health Check fehlgeschlagen – keine gültige MediTrack-Antwort auf Port ${port}"
					} else {
						echo "✅ Anwendung läuft stabil auf Port ${port} und liefert MediTrack-Startseite."
						echo "🔗 Öffne: http://localhost:${port}"
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
