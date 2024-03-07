# Dockerfile -- Gamer server manager written in Java
FROM maven:latest

WORKDIR /java-server

# Copy project
COPY ./target/*.jar /java-server/summary.jar
COPY ./target/libs/*.* /java-server/libs/
COPY ./start.sh /java-server/

RUN chmod +x /java-server/start.sh

EXPOSE 8080

# Add your script(s) here or leave it commented out
# CMD ["./.sh"]
ENTRYPOINT ["/java-server/start.sh"]