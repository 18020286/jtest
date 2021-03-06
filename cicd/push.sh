#!/bin/sh
set -e

VERSION=$1

echo "Push image to registry server"
docker --config ~/.docker/.phuhk push 10.60.156.72/mve/mve-auth-service:$VERSION
docker rmi 10.60.156.72/mve/mve-auth-service:$VERSION
echo "Finish push image to registry server"
