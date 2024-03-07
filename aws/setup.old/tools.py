import json
import logging

from confluent_kafka.cimpl import NewTopic
from confluent_kafka.admin import AdminClient

from application.dependencies.utils.producerservice import ProducerService


class Tools:
    producer: ProducerService = None
    admin_client: AdminClient = None

    def __init__(self):
        self.producer = ProducerService("tools")
        self.admin_client = AdminClient(self.producer.cluster.conf)

    def create_topic(self, topic: str):
        topics = self.admin_client.list_topics(timeout=10)
        if topic in topics.topics:
            return False

        logging.info("Creating topic: {}".format(topic))

        new_topic: NewTopic = NewTopic(topic,
                                       num_partitions=1,
                                       replication_factor=3,
                                       config={
                                           "cleanup.policy": "compact",
                                       })

        create_result: dict = self.admin_client.create_topics([new_topic])
        create_result[topic].result(60)

        logging.info("Topic created: {}".format(topic))
        return True

    def publish(self, topic: str, key: str, value: any):
        if type(value) is dict:
            value = json.dumps(value)

        logging.info("Publishing to topic: {}".format(topic))
        self.producer.produce(key, value, topic)
        logging.info("Published to topic: {}".format(topic))
