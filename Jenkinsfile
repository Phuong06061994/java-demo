pipeline {
    agent { label 'java-slave' }

    environment {
        DOCKER_CREDENTIALS = 'git-hub-credential'
        DOCKER_IMAGE_NAME = 'phuong06061994/java-demo'
        IMAGE_TAG = "${env.BUILD_ID}"
        BACKEND_HOST = "backend-app"  // Name of the backend container for SSH
        SSH_PORT = "2222"  // SSH port for backend
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout the code from your repository
                checkout scm
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Build the Docker image, specifying the context
                    sh """
                        docker build -t ${DOCKER_IMAGE_NAME}:${IMAGE_TAG} .
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

        stage('SSH to Backend, Pull, and Run Image') {
            steps {
                script {
                    // SSH into backend, pull the Docker image, and run it
                    sh """
                        ssh -o StrictHostKeyChecking=no -p $SSH_PORT root@$BACKEND_HOST << EOF
                            docker pull ${DOCKER_IMAGE_NAME}:${IMAGE_TAG}
                            docker run -d --name backend-container ${DOCKER_IMAGE_NAME}:${IMAGE_TAG}
                        EOF
                    """
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
