package io.confluent.pie.csp_demo.aws.bot;

import io.confluent.pie.csp_demo.aws.common.caches.DiscussionsCache;
import io.confluent.pie.csp_demo.aws.common.models.ChatInput;
import io.confluent.pie.csp_demo.aws.common.models.ChatOutput;
import io.confluent.pie.csp_demo.aws.common.models.Discussion;
import io.confluent.pie.csp_demo.aws.bot.processors.ChatProcessing;
import io.confluent.pie.csp_demo.aws.common.utils.BedrockService;
import io.confluent.pie.csp_demo.aws.common.utils.KafkaJsonDeserializer;
import io.confluent.pie.csp_demo.aws.common.utils.KafkaJsonSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Chat processing service
 */
@Component
@Slf4j
public class ChatProcessingService {

    private final PromptProvider promptProvider;
    private final BedrockService bedrockService;
    private final DiscussionsCache discussionsCache;

    public ChatProcessingService(@Autowired PromptProvider promptProvider,
                                 @Autowired BedrockService bedrockService,
                                 @Autowired DiscussionsCache discussionsCache) {
        this.promptProvider = promptProvider;
        this.bedrockService = bedrockService;
        this.discussionsCache = discussionsCache;
    }

    @Autowired
    public void process(StreamsBuilder builder) {
        KStream<String, ChatInput> messageStream = builder
                .stream("chat_input",
                        Consumed.with(Serdes.String(),
                                Serdes.serdeFrom(new KafkaJsonSerializer<>(),
                                        new KafkaJsonDeserializer<>(ChatInput.class))));

        KStream<String, Discussion> outputKStream = messageStream.process(() -> new ChatProcessing(promptProvider,
                bedrockService,
                discussionsCache), Named.as("chat_processor"));

        // Response to user
        outputKStream.map((key, value) -> KeyValue.pair(key, value.getChat_output()))
                .to("chat_output", Produced.with(Serdes.String(),
                        Serdes.serdeFrom(new KafkaJsonSerializer<>(),
                                new KafkaJsonDeserializer<>(ChatOutput.class))));

        // Save discussion to Kafka
        outputKStream.to("chat_discussion", Produced.with(Serdes.String(),
                Serdes.serdeFrom(new KafkaJsonSerializer<>(),
                        new KafkaJsonDeserializer<>(Discussion.class))));
    }

}
