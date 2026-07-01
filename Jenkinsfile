pipeline {
    agent { 
        label 'zuvmljenson02'
    }
    environment {
        BUILD_IMAGE = "registry.lksnext.com/devsecops/maven-java-17:2.0"
        SONAR_HOST_URL = "https://sonarqube.devops.lksnext.com/"
        SONAR_TOKEN = credentials('sonar-analysis-token')
        SONAR_BRANCH = "${env.BRANCH_NAME}"
        VULNZ_URL = "https://vulnz.devops.lksnext.com"
    }
    stages {
        stage('Dependency-Check') {
            steps {
                script {
                    sh '''
                        docker run --rm \
                            -v ./:/app \
                            -v "/home/jenkins/.m2":"/home/jenkins/.m2" \
                            -e JOB_ACTION="compile" \
                            -e MAVEN_CMD="clean verify dependency-check:check -DfailBuildOnCVSS=11 -Dformat=ALL -DnvdDatafeedUrl=$VULNZ_URL -DossindexAnalyzerEnabled=false -Pcoverage" \
                            $BUILD_IMAGE
                    '''
                }
            }
        }
        stage('Sonar') {
            steps {
                script {
                    sh '''
                        docker run --rm \
                            -v ./:/app \
                            -v "/home/jenkins/.m2":"/home/jenkins/.m2" \
                            -e JOB_ACTION="compile" \
                            -e MAVEN_CMD="sonar:sonar -Dsonar.host.url=$SONAR_HOST_URL -Dsonar.token=$SONAR_TOKEN -Dsonar.dependencyCheck.xmlReportPath=target/dependency-check-report.xml -Dsonar.dependencyCheck.htmlReportPath=target/dependency-check-report.html -Dsonar.dependencyCheck.jsonReportPath=target/dependency-check-report.json" \
                            $BUILD_IMAGE
                    '''
                }
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: 'target/dependency-check-report.*', allowEmptyArchive: true
            deleteDir()
        }
    }
}
