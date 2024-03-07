package io.confluent.pie.csp_demo.aws.submit.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

@Slf4j
public class InfoSerializer implements Serializer<Map<String, Object>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, Map<String, Object> stringObjectMap) {
        try {
            return objectMapper.writeValueAsBytes(stringObjectMap);
        } catch (JsonProcessingException e) {
            log.error("Error serializing info", e);
            throw new SerializationException("Error serializing info", e);
        }
    }

    @Override
    public byte[] serialize(String topic, Headers headers, Map<String, Object> data) {
        return this.serialize(topic, data);
    }
}
