[![Build Status](https://travis-ci.org/jesstruck/jenkins-docker.svg?branch=master)](https://travis-ci.org/jesstruck/jenkins-docker)

![](https://img.shields.io/github/stars/jesstruck/jenkins-docker.svg)
![](https://img.shields.io/github/forks/jesstruck/jenkins-docker.svg)
![](https://img.shields.io/github/watchers/jesstruck/jenkins-docker.svg)
![](https://img.shields.io/github/tag/jesstruck/jenkins-docker.svg)
![](https://img.shields.io/github/release/jesstruck/jenkins-docker.svg)
![](https://img.shields.io/github/issues/jesstruck/jenkins-docker.svg)




# Commands
## Removing containers,
docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)

## Removing images
docker rmi -f $(docker images -a -q)

## Build & Run
docker build -t jes/jenkins:0.1 . ; docker run -it -p 80:8080 jes/jenkins:0.1
