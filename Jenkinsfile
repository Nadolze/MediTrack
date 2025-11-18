pipeline {
agent any
options {
// verhindert parallele Builds
disableConcurrentBuilds()
timeout(time: 30, unit: 'MINUTES')
}
environment {
DOCKER_IMAGE_PREFIX = "meditrack"
BASE_PORT = 9090
MAX_CPU = "1"
MAX_MEM = "500m"
MAVEN_HOME = tool name: 'Maven_3.9.11', type: 'maven'
}
stages {
stage('Checkout SCM') {
steps {
git branch: "${env.BRANCH_NAME ?: 'main'}",
url: '[git@github.com](mailto:git@github.com):Nadolze/MediTrack.git',
credentialsId: 'github-meditrack-key'
}
}

```
    stage('Build with Maven') {
        steps {
            script {
                // Maven Build, limitiert auf 1 CPU / 500MB durch Docker oder JVM Optionen
                sh """
                    ${MAVEN_HOME}/bin/mvn clean package -DskipTests -T 1C -Dmaven.compiler.fork=false
                """
            }
        }
    }

    stage('Build Docker Image') {
        steps {
            script {
                def branch = env.BRANCH_NAME ?: 'main'
                def imageTag = "${DOCKER_IMAGE_PREFIX}:${branch}"
                sh "docker build -t ${imageTag} ."
            }
        }
    }

    stage('Run Docker Container') {
        steps {
            script {
                def branch = env.BRANCH_NAME ?: 'main'
                def basePort = 9090
                def portMap = ['main':9090, 'test':9091] // feste Zuordnung
                def port = portMap.get(branch, basePort + env.BUILD_NUMBER.toInteger())

                // vorher ggf. alten Container entfernen
                sh "docker rm -f ${branch} || true"

                // Container starten mit Limits
                sh """
                    docker run -d --name ${branch} -p ${port}:${port} \\
                    --cpus=${MAX_CPU} --memory=${MAX_MEM} \\
                    ${DOCKER_IMAGE_PREFIX}:${branch} \\
                    --server.port=${port} -Xmx${MAX_MEM} -XX:ActiveProcessorCount=1
                """
            }
        }
    }
}

post {
    always {
        echo "Build finished for ${env.BRANCH_NAME ?: 'main'}"
    }
    failure {
        echo "Build failed!"
    }
}
```

}
