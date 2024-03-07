package io.confluent.pie.csp_demo.aws.common.models;

import io.confluent.pie.csp_demo.aws.common.utils.KafkaJsonDeserializer;

public class ChatOutputDeserializer extends KafkaJsonDeserializer<ChatOutput> {
    public ChatOutputDeserializer() {
        super(ChatOutput.class);
    }
}
