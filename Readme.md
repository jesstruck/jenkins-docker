# Commands
## Removing containers,
docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)

## Removing images
docker rmi -f $(docker images -a -q)

## Building
docker build -t jes/jenkins:0.1 .

## Running
docker run -it -p 80:8080 jes/jenkins:0.1
