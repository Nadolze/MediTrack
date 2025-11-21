// Jenkinsfile (Declarative Pipeline)
pipeline {
  agent any

  environment {
    APP_DIR = "/opt/meditrack"
    SERVICE_TEMPLATE = "/opt/meditrack/meditrack-service.template"
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build') {
      steps {
        sh 'mvn -B -DskipTests package'
      }
    }

    stage('Prepare Deploy') {
      steps {
        script {
          // Branchname und sanitize (systemd service names dürfen keine Schrägstriche)
          env.BRANCH = env.BRANCH_NAME.replaceAll(/[\/\\]/, '-')
          // Port mapping
          def portMap = ['main': '9090', 'test': '9091', 'features': '9092']
          env.PORT = portMap.containsKey(env.BRANCH_NAME) ? portMap[env.BRANCH_NAME] : sh(script: "bash -lc 'for p in \$(seq 9093 9200); do if ! ss -ltn | awk \"{print \\$4}\" | grep -q :\${p}\"; then echo \$p; break; fi; done'", returnStdout: true).trim()
          if (!env.PORT) { error "Kein freier Port gefunden" }

          // Pfad zum Jar
          def artifact = sh(script: "ls target/*.jar | head -n1", returnStdout: true).trim()
          if (!artifact) { error "Kein Jar in target/ gefunden" }
          env.ARTIFACT = artifact
          echo "Branch ${env.BRANCH_NAME} -> sanitized ${env.BRANCH}, Port ${env.PORT}, Jar: ${env.ARTIFACT}"
        }
      }
    }

    stage('Deploy') {
      steps {
        script {
          // Kopiere Jar nach /opt/meditrack/<branch>.jar (überschreiben)
          sh "cp ${env.ARTIFACT} ${APP_DIR}/${env.BRANCH}.jar"
          sh "chmod 644 ${APP_DIR}/${env.BRANCH}.jar"

          // Erzeuge systemd Service via Template (ersetze Platzhalter)
          sh """
            cat > /etc/systemd/system/meditrack-${env.BRANCH}.service <<'SERVICE'
[Unit]
Description=MediTrack - ${env.BRANCH}
After=network.target

[Service]
User=root
WorkingDirectory=${APP_DIR}
ExecStart=/usr/bin/java -jar ${APP_DIR}/${env.BRANCH}.jar --server.port=${env.PORT} --spring.datasource.url='jdbc:mysql://82.165.255.70:3306/meditrack?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC' --spring.datasource.username=web_user --spring.datasource.password='Web_pass123!' --spring.jpa.hibernate.ddl-auto=update
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
SERVICE
          """

          // systemd neu laden und Service restarten
          sh "systemctl daemon-reload || true"
          sh "systemctl enable --now meditrack-${env.BRANCH}.service || true"
          sh "systemctl restart meditrack-${env.BRANCH}.service || true"
          sh "systemctl status meditrack-${env.BRANCH}.service --no-pager || true"
        }
      }
    }
  }

  post {
    failure {
      sh 'echo "Build oder Deploy fehlgeschlagen"'
    }
  }
}
