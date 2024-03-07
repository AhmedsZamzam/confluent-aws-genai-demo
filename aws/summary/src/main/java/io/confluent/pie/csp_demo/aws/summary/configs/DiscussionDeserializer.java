package io.confluent.pie.csp_demo.aws.summary.configs;

import io.confluent.pie.csp_demo.aws.common.models.Discussion;
import io.confluent.pie.csp_demo.aws.common.utils.KafkaJsonDeserializer;

public class DiscussionDeserializer extends KafkaJsonDeserializer<Discussion> {
    public DiscussionDeserializer() {
        super(Discussion.class);
    }

}
