pipeline {
    agent {label 'java-slave-fff10b55'}

    environment {
        JAVA_HOME = tool name: 'JDK 17', type: 'JDK'
        MAVEN_HOME = tool name: 'Maven 3', type: 'Tool'
        IMAGE_NAME = 'phuong06061994/java-demo'  // Replace with your Docker Hub username and image name
        DOCKER_REGISTRY = 'docker.io'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Java Project') {
            steps {
                script {
                    // Run Maven clean and install to build the project
                    sh "'${MAVEN_HOME}/bin/mvn' clean install"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Build the Docker image using the Dockerfile in the repository
                    sh "docker build -t ${DOCKER_REGISTRY}/${IMAGE_NAME}:${BUILD_NUMBER} ."
                }
            }
        }

        stage('Login to Docker Hub') {
            steps {
                script {
                    // Login to Docker Hub using Jenkins credentials
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentail', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh "echo ${DOCKER_PASSWORD} | docker login -u ${DOCKER_USERNAME} --password-stdin"
                    }
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    // Push the Docker image to Docker Hub
                    sh "docker push ${DOCKER_REGISTRY}/${IMAGE_NAME}:${BUILD_NUMBER}"
                }
            }
        }

        stage('Cleanup') {
            steps {
                script {
                    // Remove the Docker image after pushing to save space
                    sh "docker rmi ${DOCKER_REGISTRY}/${IMAGE_NAME}:${BUILD_NUMBER}"
                }
            }
        }
    }

    post {
        always {
            cleanWs()  // Clean up the workspace after the build
        }
    }
}
