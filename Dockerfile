FROM mozilla/sbt
MAINTAINER Shreyas
WORKDIR /app
RUN apt-get update && apt-get -y install dos2unix && apt-get -y install vim
COPY . .
EXPOSE 80 9003 8003 10003
ENTRYPOINT ["sh", "./start.sh"]