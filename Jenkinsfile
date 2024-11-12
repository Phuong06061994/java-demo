pipeline {
    agent { label 'java-slave' }

    environment {
        DOCKER_CREDENTIALS = 'dockerhub-credential' // This is your Jenkins credentials ID
        DOCKER_IMAGE_NAME = 'phuong06061994/angular-demo'
        IMAGE_TAG = "${env.BUILD_ID}"
        GIT_CREDENTIALS = 'github-credential'  // Jenkins GitHub credentials ID
        GIT_REPO_URL = 'https://github.com/Phuong06061994/angular-demo.git'  // Replace with your repo URL
        REMOTE_HOST = 'ec2-user@ec2-54-174-102-199.compute-1.amazonaws.com'
        SSH_CREDENTIALS = 'ec2-credential'
        REMOTE_COMPOSE_PATH = '/home/ec2-user/angular-demo' // Adjusted to a typical Linux path
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
                    IMAGE_TAG = sh(
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
                    sh """
                        docker build -t ${DOCKER_IMAGE_NAME}:${IMAGE_TAG} -f my-app/Dockerfile my-app
                    """
                }
            }
        }

        stage('Login to Docker Hub') {
            steps {
                script {
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

        stage('Copy docker-compose.yml to Remote Host') {
            steps {
                script {
                    // Copy the updated docker-compose.yml to the remote host
                    sshagent([SSH_CREDENTIALS]) {
                        sh """
                            scp -o StrictHostKeyChecking=no docker-compose.yml ${REMOTE_HOST}:${REMOTE_COMPOSE_PATH}/docker-compose.yml
                        """
                    }
                }
            }
        }

        stage('Deploy on Remote Host') {
            steps {
                script {
                    sshagent([SSH_CREDENTIALS]) {
                        sh """
                            ssh -o StrictHostKeyChecking=no ${REMOTE_HOST} '
                                cd ${REMOTE_COMPOSE_PATH}
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
            sh "docker system prune -af"
        }
    }
}
