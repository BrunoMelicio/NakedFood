#!/bin/bash

cd AndroidApp
chmod +x gradlew

./gradlew assembleDebug

# Firebase service account decrypt
openssl aes-256-cbc -K $encrypted_16a4fefa7589_key -iv $encrypted_16a4fefa7589_iv -in service-account.json.enc -out service-account.json -d

# Firebase setup
wget --quiet --output-document=/tmp/google-cloud-sdk.tar.gz https://dl.google.com/dl/cloudsdk/channels/rapid/google-cloud-sdk.tar.gz  
mkdir -p /opt  
tar zxf /tmp/google-cloud-sdk.tar.gz --directory /opt  
/opt/google-cloud-sdk/install.sh --quiet
source /opt/google-cloud-sdk/path.bash.inc

# Setup and configure the project
gcloud components update  
#echo $CLOUD_PROJECT_ID  
gcloud config set project nakedfoodv1

# Activate cloud credentials
gcloud auth activate-service-account --key-file service-account.json

# List available options for logging purpose only (so that we can review available options)
gcloud firebase test android models list  
gcloud firebase test android versions list

gcloud firebase test android run --app app/build/outputs/apk/debug/app-debug.apk --device model=Pixel2,version=27,locale=en,orientation=portrait