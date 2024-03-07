# CSP Demo

## Aim

The aim of this project is to create a python flask app that will collect a response, push it to a kafka topic and then interact with an AI chatbot

### Start the flask app
At the top level of the directory run the following command `python application/app.py`. This will start the app running on `127.0.0.1:5000`


### Configuration set up

To configure the listed values, update the `application/config.ini` file:
* server - This is the Bootstrap server used by Confluent Cloud
* topic - This is the topic name that the producer will push data too

Environmental Variables should be set in the IDE:
* SSL_USERNAME - This is the KEY value from Confluent Cloud
* SSL_PASSWORD - This is the SECRET from Confluent Cloud
