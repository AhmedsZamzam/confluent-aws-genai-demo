package io.confluent.pie.csp_demo.aws.setup.utils;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;

public class KafkaUtils {
    
    /**
     * Create a compacted topic
     *
     * @param topicName Topic name
     * @return Created topic
     */
    public static NewTopic createCompactedTopic(final String topicName) {
        return TopicBuilder
                .name(topicName)
                .replicas(3)
                .compact()
                .partitions(1)
                .build();
    }

    /**
     * Create a topic
     *
     * @param topicName Topic name
     * @return Created topic
     */
    public static NewTopic createTopic(final String topicName) {
        return TopicBuilder
                .name(topicName)
                .replicas(3)
                .partitions(1)
                .build();
    }


}
