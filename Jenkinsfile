pipeline {
    agent { label 'java-slave' }

    environment {
        DOCKER_CREDENTIALS = 'dockerhub-credential'
        DOCKER_IMAGE_NAME = 'phuong06061994/angular-demo'
        IMAGE_TAG = "${env.BUILD_ID}"
        GIT_CREDENTIALS = 'github-credential'
        GIT_REPO_URL = 'https://github.com/Phuong06061994/angular-demo.git'
        REMOTE_HOST = 'ec2-user@ec2-54-174-102-199.compute-1.amazonaws.com'
        SSH_CREDENTIALS = 'ec2-credential'
        REMOTE_COMPOSE_PATH = '/home/ec2-user/angular-demo'
    }

    stages {
        stage('Checkout') {
            steps {
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

        stage('Prepare Remote Directory and Copy docker-compose.yml') {
            steps {
                script {
                    sshagent([SSH_CREDENTIALS]) {
                        // Create the directory if it does not exist, then copy docker-compose.yml
                        sh """
                            ssh -o StrictHostKeyChecking=no ${REMOTE_HOST} 'mkdir -p ${REMOTE_COMPOSE_PATH}'
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
