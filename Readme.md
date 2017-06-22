# Removing containers,
docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)

# Removing images
docker rmi $(docker images -a -q)



# Building
docker build -t jes/jenkins:0.1 .
