package io.confluent.pie.csp_demo.aws.common.caches;

import io.confluent.pie.csp_demo.aws.common.models.Discussions;
import io.confluent.pie.csp_demo.aws.common.utils.KafkaJsonDeserializer;
import io.confluent.pie.csp_demo.aws.common.utils.KafkaJsonSerializer;
import io.kcache.KafkaCacheConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * DiscussionsCache is a Kafka-backed cache for Discussions objects.
 */
@Component
public class DiscussionsCache extends AbstractCache<Discussions> {
    protected DiscussionsCache(@Autowired KafkaCacheConfig cfg) {
        super(cfg, new KafkaJsonSerializer<>(), new KafkaJsonDeserializer<>(Discussions.class), "discussions");
    }
}