pipeline {
    agent any

    environment {
        // Basis-Port für Feature-Branches
        BASE_PORT = 9092
    }
tools {
        maven 'Maven_3.9.11'
    }
    stages {
        stage('Determine Port') {
            steps {
                script {
                    def branch = env.BRANCH_NAME

                    if (branch == "main") {
                        PORT = 9090
                    } else if (branch == "test") {
                        PORT = 9091
                    } else {
                        // Hole alle Branch-Namen aus Git
                        def branches = sh(
                            script: "git for-each-ref --format='%(refname:short)' refs/heads | sort",
                            returnStdout: true
                        ).trim().split('\n')

                        // Filtere "main" und "test" raus
                        def featureBranches = branches.findAll { it != "main" && it != "test" }

                        // Position in der alphabetischen Liste + 9092
                        def index = featureBranches.indexOf(branch)
                        if (index < 0) {
                            error "Aktueller Branch taucht nicht in der Feature-Liste auf!"
                        }

                        PORT = BASE_PORT.toInteger() + index
                    }

                    echo "👉 Branch '${branch}' wird auf Port ${PORT} laufen."
                }
            }
        }

        stage('Build') {
            steps {
                sh "mvn -v"
                sh "mvn clean package -DskipTests"
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // zuerst alte Instanz killen
                    sh """
                    echo Kill vorhandene Instanz auf Port ${PORT}
                    PID=\$(lsof -t -i:${PORT} || true)
                    if [ ! -z "\$PID" ]; then
                        kill -9 \$PID
                    fi
                    """

                    // starten
                    sh """
                    echo Starte Server auf Port ${PORT}
                    nohup java -jar target/*.jar --server.port=${PORT} >/dev/null 2>&1 &
                    """
                }
            }
        }
    }

    post {
        always {
            echo "🚀 Deploy abgeschlossen: Branch '${env.BRANCH_NAME}' läuft nun auf Port ${PORT}"
        }
    }
}
