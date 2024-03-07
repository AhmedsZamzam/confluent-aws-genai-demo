package io.confluent.pie.csp_demo.aws.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

@Slf4j
public class KafkaJsonDeserializer<T> implements Deserializer<T> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Class<T> tClas;

    public KafkaJsonDeserializer(Class<T> tClass) {
        this.tClas = tClass;
    }

    @Override
    public T deserialize(String s, byte[] bytes) {
        try {
            return mapper.readValue(bytes, tClas);
        } catch (IOException e) {
            log.error("Error deserializing from topic '{}'.", s, e);
            throw new KafkaException(e);
        }
    }

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        return deserialize(topic, data);
    }

}
