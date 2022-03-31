pipeline {
        environment{
            registry = 'keoffor/user-service'
            dockerHubCreds = 'docker_hub'
            dockerImage = ''
            deploymentFile = 'src/kubernetes/deployment.yml'
        }
    agent any
    stages {
        stage('Test') {
            when{
            branch 'master'
            }
          steps {
            sh 'ls $WORKSPACE '
            sh 'mvn -v'
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
                            sh 'echo "Hello World"'
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
//         stage('Wait for approval') {
//                 when {
//                     branch 'main'
//                 }
//                 steps {
//                         dir("project2FrontEnd") {
//                     script {
//                         try {
//                             timeout(time: 1, unit: 'MINUTES') {
//                                 approved = input message: 'Deploy to production?', ok: 'Continue',
//                                                    parameters: [choice(name: 'approved', choices: 'Yes\nNo', description: 'Deploy build to production')]
//                                 if(approved != 'Yes') {
//                                     error('Build did not pass approval')
//                                 }
//                             }
//                         } catch(error) {
//                             error('Build failed because timeout was exceeded')
//                             }
//                         }
//                     }
//                 }
//             }
           stage('Deploy to GKE') {
                   when {
                       branch 'master'
                   }
                   steps{

                         sh 'sed -i "s/%TAG%/$BUILD_NUMBER/g" ./src/Kubernetes/deployment.yml'
                         sh 'cat ./src/Kubernetes/deployment.yml'
                         sh 'kubectl get pods'
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
  }
}