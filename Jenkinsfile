pipeline {
        environment{
            registry = 'keoffor/user-service'
            dockerHubCreds = 'docker_hub'
            dockerImage = ''
            deploymentFile = 'kubernetes/deployment.yml'
        }
    agent any
    stages {
        stage('Test') {
            when{
            branch 'master'
            }
          steps {
            sh 'ls $WORKSPACE '
            dir("User-Service") {
            sh 'echo "Hello World"'
              withMaven {
                sh 'mvn test'
              }
            }
            }
          }
          stage('Build') {
               when {
                   branch 'master'
               }
               steps {
                    sh 'ls $WORKSPACE '
                            dir("User-Service") {
                            sh 'echo "Hello World"'
                   withMaven {
                       sh 'mvn clean package -DskipTests'
              }
            }
          }
        }
        stage('Docker Build') {
                   when {
                       branch 'master'
                   }
                   steps {
                        dir("User-Service") {
                       script {
                           echo "$registry:$currentBuild.number"
                           dockerImage = docker.build "$registry"
                       }
                   }
               }
             }
             stage('Docker Deliver') {
                     when {
                         branch 'master'
                     }
                     steps {
                             dir("User-Service") {
                         script {
                             docker.withRegistry('', dockerHubCreds) {
                                 dockerImage.push("$currentBuild.number")
                                 dockerImage.push("latest")

                  }
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
//            stage('Deploy to GKE') {
//                    when {
//                        branch 'main'
//                    }
//                    steps{
//                         echo "build deployment " + deploymentFile
//                         dir("project2FrontEnd") {
//
//                          sh 'sed -i "s/%TAG%/$BUILD_NUMBER/g" ./k8s/api1.yml'
//                          sh 'cat ./k8s/api1.yml'
//                        step([$class: 'KubernetesEngineBuilder',
//                            projectId: 'macro-key-339512',
//                            clusterName: 'macro-key-339512-gke',
//                            zone: 'us-central1',
//                            manifestPattern: 'k8s/',
//                            credentialsId: 'macro-key-339512',
//                            verifyDeployments: true
//                        ])
//           }
//       }
//     }
  }
}