[![Build Status](https://travis-ci.org/jesstruck/jenkins-docker.svg?branch=master)](https://travis-ci.org/jesstruck/jenkins-docker)
[![Waffle.io - Columns and their card count](https://badge.waffle.io/jesstruck/jenkins-docker.svg?columns=inbox,inProgress,review&style=flat)](http://waffle.io/jesstruck/jenkins-docker)


# Commands
## Removing containers,
docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)

## Removing images
docker rmi -f $(docker images -a -q)

## Build & Run
docker build -t jes/jenkins:0.1 . ; docker run -it -p 80:8080 jes/jenkins:0.1
