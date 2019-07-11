
pipeline {
    options {
        buildDiscarder(logRotator(numToKeepStr: '5', artifactNumToKeepStr: '30'))
    }
    agent any

    parameters {
        booleanParam(defaultValue: false, description: 'Release and publish', name: "releaseAndPublish")
    }

    tools {
        jdk 'jdk8'
        maven 'default'
    }

    stages {

        /*******************************************************/
        /*   Maven install including tests and Sonar analysis  */
        /*******************************************************/

        stage('[Build] - Clean workspace') {
            steps {

                sh "mvn clean"

            }
        }

        stage('[Build] - Tests + Sonar') {
            steps {

                dir('mib-netutils') {
                    sh "mvn -Dsonar.host.url=http://10.1.126.9:9000 test sonar:sonar"
                }

            }
        }

        stage("[Analysis] Code quality") {

            steps {

                script {
                    def projectName  = "com.mailinblack%3Amib-netutils"
                    def sonarBaseUrl = "http://10.1.126.9:9000/"

                    def sonarProjectUrl  = sonarBaseUrl + "dashboard?id=" + projectName
                    def statusRequestUrl = sonarBaseUrl + "api/project_branches/list?project=" + projectName

                    def statusRequestContent = statusRequestUrl.toURL().text

                    println("Content of status request:" + statusRequestContent);

                    def json = new groovy.json.JsonSlurper().parseText(statusRequestContent)
                    def qualityStatusOfBranch = json.branches.find {it.name == env.BRANCH_NAME}
                    if(qualityStatusOfBranch.status.qualityGateStatus.equals("ERROR")) {
                        currentBuild.result = 'UNSTABLE'
                        currentBuild.displayName = "Sonar failure"
                        currentBuild.description = "<a href=\"" + sonarProjectUrl + "\">Sonar failed on new code</a>"
                    }

                }

            }

        }

        stage('[Publishing] Snapshot') {

            when {
                expression {
                    return !params.releaseAndPublish
                }
            }

            steps {
                sh "mvn -DskipTests deploy"
            }

        }

        stage('[Publishing] Release and publish') {

            when {
                expression {
                    return params.releaseAndPublish
                }
            }

            steps {
                // TODO
                println("TODO")
            }

        }

    }
}