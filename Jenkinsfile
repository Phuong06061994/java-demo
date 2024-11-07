pipeline {
    agent { label 'java-slave-339accbd' }

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
        
    }
}

