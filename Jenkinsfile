pipeline {
    agent { 
        label 'zuvmljenson02'
    }
    environment {
        BUILD_IMAGE = "registry.lksnext.com/devsecops/maven-java-17:2.0"
        SONAR_HOST_URL = "https://sonarqubeenterprise.devops.lksnext.com/"
        SONAR_TOKEN = credentials('sonar-analysis-token')
    }
    stages {
        stage('Sonar') {
            when {
                environment name: 'JOB_ACTION', value: 'sonar'
            }
            steps {
                script {
                    sh '''
                        docker run --rm \
                            -v ./:/app \
                            -v "/home/jenkins/.m2":"/home/jenkins/.m2" \
                            -e JOB_ACTION="compile" \
                            -e MAVEN_CMD="clean verify sonar:sonar -Dsonar.host.url=$SONAR_HOST_URL -Dsonar.token=$SONAR_TOKEN" \
                            $BUILD_IMAGE
                    '''
                }
            }
        }
    }
    post {
        always {
            deleteDir()
        }
    }
}
