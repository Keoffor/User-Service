pipeline {
        environment{
            registry = 'keoffor/user-service'
            dockerHubCreds = 'docker_hub'
            dockerImage = ''
            deploymentFile = 'src/kubernetes/deployment.yml'
            PATH = "$PATH:/usr/share/maven/bin"
        }
    agent any
    stages {
        stage('Test') {
            when{
            branch 'master'
            }
          steps {
            sh 'ls $WORKSPACE '
            sh 'echo "Hello World"'
              withMaven {
                sh 'mvn test'
              }
            }
          }
          stage('Build') {
               when {
                   branch 'master'
               }
               steps {
                    sh 'ls $WORKSPACE '
                   withMaven {
                       sh 'mvn clean package -DskipTests'
              }
          }
        }
        stage('Docker Build') {
                   when {
                       branch 'master'
                   }
                   steps {
                       script {
                           echo "$registry:$currentBuild.number"
                           dockerImage = docker.build "$registry"
                       }
                   }
             }
             stage('Docker Deliver') {
                     when {
                         branch 'master'
                     }
                     steps {
                         script {
                             docker.withRegistry('', dockerHubCreds) {
                                 dockerImage.push("$currentBuild.number")
                                dockerImage.push("latest")

              }
            }
          }
        }
        stage('Sonar scan') {
        //    def scannerHome = tool 'SonarScanner 4.0';
                steps{
                withSonarQubeEnv('sonar1') {
                // If you have configured more than one global server connection, you can specify its name
        //      sh "${scannerHome}/bin/sonar-scanner"
                sh "mvn sonar:sonar"
            }
           }
         }

           stage('Deploy to GKE') {
                   when {
                       branch 'master'
                   }
                   steps{

                         sh 'sed -i "s/%TAG%/$BUILD_NUMBER/g" ./src/Kubernetes/deployment.yml'
                         sh 'cat ./src/Kubernetes/deployment.yml'
                       step([$class: 'KubernetesEngineBuilder',
                           projectId: 'stagingpro',
                           clusterName: 'stagingpro-gke',
                           zone: 'us-central1',
                           manifestPattern: 'src/Kubernetes/',
                           credentialsId: 'stagingpro',
                           verifyDeployments: true
                       ])
       }
    }
    post{
        always{
            cleanWs()
        }
      }
  }
}