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

					// === Portlogik ===================================
					def currentUser = isUnix() ? sh(script: "whoami", returnStdout: true).trim() : bat(script: "echo %USERNAME%", returnStdout: true).trim()
					def meditrackPort = "8080"

					// Wenn du (wolfdeleu oder micro) baust → Port 9090 erzwingen
					if (currentUser.toLowerCase().contains("wolfdeleu") || currentUser.toLowerCase().contains("micro")) {
						echo "👤 Build durch ${currentUser} erkannt – MediTrack läuft auf 9090 (kein Fallback)."
						meditrackPort = "9090"
					} else {
						echo "👥 Standard-Build – MediTrack läuft auf 8080."
					}

					def deployDir = isUnix() ? "/opt/meditrack/main" : "C:\\meditrack\\main"
					def appJar = "mediweb-0.0.1-SNAPSHOT.jar"

					// === Deployment-Ordner vorbereiten ===============
					if (isUnix()) {
						sh "mkdir -p ${deployDir}"
						sh "cp target/${appJar} ${deployDir}/"
						sh "fuser -k ${meditrackPort}/tcp || true"
						sh "nohup java -jar ${deployDir}/${appJar} --server.port=${meditrackPort} > ${deployDir}/app.log 2>&1 &"
					} else {
						bat "if not exist ${deployDir} mkdir ${deployDir}"
						bat "copy target\\${appJar} ${deployDir}\\ /Y"

						// MediTrack stoppen (nicht Jenkins)
						bat "powershell -Command \"Get-WmiObject Win32_Process | Where-Object { \$_.CommandLine -match 'mediweb-0.0.1-SNAPSHOT.jar' } | ForEach-Object { Stop-Process -Id \$_.ProcessId -Force -ErrorAction SilentlyContinue }\""

						// Neues Startskript erzeugen
						bat """
echo @echo off > ${deployDir}\\start_meditrack.bat
echo cd /d ${deployDir} >> ${deployDir}\\start_meditrack.bat
echo java -jar ${appJar} --server.port=${meditrackPort} >> ${deployDir}\\start_meditrack.bat
start "" /min cmd /c ${deployDir}\\start_meditrack.bat
"""
						echo "🚀 MediTrack wurde in eigenem Prozess gestartet (Port ${meditrackPort})"
					}

					env.ACTIVE_PORT = meditrackPort
				}
			}
		}

		stage('Health Check') {
			steps {
				script {
					def port = env.ACTIVE_PORT ?: "9090"
					echo "🔍 Überprüfe Erreichbarkeit und Inhalt auf http://localhost:${port}"

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
