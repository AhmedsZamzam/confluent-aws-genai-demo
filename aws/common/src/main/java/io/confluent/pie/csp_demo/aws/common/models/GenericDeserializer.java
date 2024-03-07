package io.confluent.pie.csp_demo.aws.common.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GenericDeserializer implements Deserializer<Map<String, Object>> {

    private final TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {
    };
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public Map<String, Object> deserialize(String s, byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, typeRef);
        } catch (IOException e) {
            throw new KafkaException("Error deserializing object", e);
        }
    }

    @Override
    public Map<String, Object> deserialize(String topic, Headers headers, byte[] data) {
        return deserialize(topic, data);
    }

    @Override
    public void close() {
    }
}
