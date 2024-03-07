package io.confluent.pie.csp_demo.aws.common.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class GenericSerializer implements Serializer<Map<String, Object>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String s, Map<String, Object> stringObjectMap) {
        try {
            return objectMapper.writeValueAsBytes(stringObjectMap);
        } catch (JsonProcessingException e) {
            throw new KafkaException("Error serializing object", e);
        }
    }

    @Override
    public byte[] serialize(String topic, Headers headers, Map<String, Object> data) {
        return serialize(topic, data);
    }

    @Override
    public void close() {
    }
}
