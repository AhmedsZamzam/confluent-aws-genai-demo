import json
import logging.config

from setup.tools import Tools

logging.config.fileConfig(fname='log.conf', disable_existing_loggers=False)

if __name__ == '__main__':
    tools: Tools = Tools()

    f = open('./data.json')
    data: [dict] = json.load(f)
    f.close()

    logging.info("Loading {} records".format(len(data)))

    ignore_existing_topics: bool = False
    for record in data:
        topic: str = record['topic']
        if 'key' not in record:
            tools.create_topic(topic)
            continue

        key: str = record['key']
        value: any = record['value']

        has_data: bool = value is not None
        if not topic == "prompts":
            continue
        if not tools.create_topic(topic) and not ignore_existing_topics and has_data:
            logging.info("Topic already exists: {}. Do you really want to send data?".format(topic))
            pressedKey = input("Y(es),N(no),A(ll): ")
            if pressedKey == 'N':
                continue
            elif pressedKey == 'A':
                ignore_existing_topics = True
                logging.info("Ignoring existing topics")

        if has_data:
            tools.publish(topic, key, value)

