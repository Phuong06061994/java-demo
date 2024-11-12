pipeline {
    agent {label 'java-slave'}

    environment {
        // Set Docker Hub credentials
        DOCKER_CREDENTIALS = 'dockerhub-credential' // This is your Jenkins credentials ID
        DOCKER_IMAGE_NAME = 'phuong06061994/angular-demo'
        IMAGE_TAG = "${env.BUILD_ID}"
        GIT_CREDENTIALS = 'github-credential'  // Jenkins GitHub credentials ID
        GIT_REPO_URL = 'https://github.com/Phuong06061994/angular-demo.git'  // Replace with your repo URL
        REMOTE_HOST = 'ec2-user@ec2-54-174-102-199.compute-1.amazonaws.com'
        SSH_CREDENTIALS = 'ec2-credential'
        REMOTE_COMPOSE_PATH = 'C:/PhuongNV63/github/angular-demo/docker-compose.yml'
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout the code from your repository
                checkout scm
            }
        }
         stage('Set Image Tag') {
                    steps {
                        script {
                            // Retrieve the short commit hash and assign it to IMAGE_TAG
                            IMAGE_TAG  = sh(
                                script: "git rev-parse --short=6 HEAD",
                                returnStdout: true
                            ).trim()

                            echo "Image Tag: ${IMAGE_TAG}"
                        }
                    }
                }
        
        stage('Build Docker Image') {
            steps {
                script {
                    // Build the Docker image, specifying the 'my-app' directory as the context
                    sh """
                        docker build -t ${DOCKER_IMAGE_NAME}:${IMAGE_TAG} -f my-app/Dockerfile my-app
                    """
                }
            }
        }
        stage('Login to Docker Hub') {
            steps {
                script {
                    // Log in to Docker Hub using stored credentials
                    withCredentials([usernamePassword(credentialsId: DOCKER_CREDENTIALS, usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh """
                            echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USERNAME --password-stdin
                        """
                    }
                }
            }
        }
        stage('Push Docker Image') {
            steps {
                script {
                    // Push the Docker image to Docker Hub
                    sh """
                        docker push ${DOCKER_IMAGE_NAME}:${IMAGE_TAG}
                    """
                }
            }
        }
         stage('Update docker-compose.yml with Tag') {
                steps {
                    script {
                        sh "sed -i 's|image: phuong06061994/angular-demo:.*|image: phuong06061994/angular-demo:${IMAGE_TAG}|' docker-compose.yml"
                    }
                }
        }
        // stage('Commit and Push Changes to GitHub') {
        //     steps {
        //         script {
        //             withCredentials([usernamePassword(credentialsId: GIT_CREDENTIALS, usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
        //                 sh """
        //                     git config user.name "Jenkins"
        //                     git config user.email "jenkins@yourdomain.com"
        //                     git add docker-compose.yml
        //                     git commit -m "Update docker-compose.yml with image tag ${IMAGE_TAG}"
        //                     git push https://\$GIT_USERNAME:\$GIT_PASSWORD@github.com/Phuong06061994/angular-demo.git HEAD:main                     
        //                 """
        //             }
        //         }
        //     }
        // }

        stage('Deploy on Remote Host') {
            steps {
                script {
                    // SSH into the remote host to update and deploy using docker-compose
                    sshagent([SSH_CREDENTIALS]) {
                        sh """
                            ssh -o StrictHostKeyChecking=no ${REMOTE_HOST} '
                                cd ${REMOTE_COMPOSE_PATH}
                                sed -i "s|image: phuong06061994/angular-demo:.*|image: phuong06061994/angular-demo:${IMAGE_TAG}|" docker-compose.yml
                                docker-compose down
                                docker-compose pull
                                docker-compose up -d
                            '
                        """
                    }
                }
            }
        }

    }
    post {
        always {
            // Clean up Docker images to avoid running out of space
            sh "docker system prune -af"
        }
    }
}
