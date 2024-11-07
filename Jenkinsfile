pipeline {
    agent {label 'java-slave-8664637d'}

    environment {
        // Set Docker Hub credentials
        DOCKER_CREDENTIALS = 'dockerhub-credentail' // This is your Jenkins credentials ID
        DOCKER_IMAGE_NAME = 'phuong06061994/java-demo'
        IMAGE_TAG = "${env.BUILD_ID}"
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
    }

    post {
        always {
            // Clean up Docker images to avoid running out of space
            sh "docker system prune -af"
        }
    }
}
