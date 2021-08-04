#!/bin/sh
set -e

VERSION=$1

echo "Run mve-auth-service"
sed -i -e "s,__MVE_AUTH_SERVICE_VERSION__,$VERSION,g" cicd/mve-auth-service-deployment.yaml
sudo kubectl -n mve apply -f cicd/mve-auth-service-deployment.yaml --kubeconfig=cicd/mve-k8s-config

echo  'Waiting for deploy'
sleep 30

echo  'View result deploy'
sudo kubectl -n mve get pods,svc --kubeconfig=cicd/mve-k8s-config

echo "Finish run mve-auth-service"
