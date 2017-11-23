#!/bin/bash
set -e

ACRS_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DOCKER_DIR=${1:-$ACRS_DIR/target/docker}
rm -rf $DOCKER_DIR
mkdir -p $DOCKER_DIR
if [ ! -f $ACRS_DIR/target/acrs-portlets.war ]; then
  mvn -f $ACRS_DIR/pom.xml clean package
fi
wget -P $DOCKER_DIR https://swift.rc.nectar.org.au:8888/v1/AUTH_ffc889d904964131af390cd202d8f603/docker/acrs-liferay.tar.gz
wget -P $DOCKER_DIR https://swift.rc.nectar.org.au:8888/v1/AUTH_ffc889d904964131af390cd202d8f603/docker/server-jre-8u152-linux-x64.tar.gz
wget -P $DOCKER_DIR https://swift.rc.nectar.org.au:8888/v1/AUTH_ffc889d904964131af390cd202d8f603/docker/ecj-4.5.jar
cp $ACRS_DIR/Dockerfile $DOCKER_DIR
cp $ACRS_DIR/target/acrs-portlets.war $DOCKER_DIR
( cd $DOCKER_DIR && docker build -t acrs . )
