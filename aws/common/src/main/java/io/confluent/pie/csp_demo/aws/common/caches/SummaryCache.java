package io.confluent.pie.csp_demo.aws.common.caches;

import io.confluent.pie.csp_demo.aws.common.models.Summary;
import io.confluent.pie.csp_demo.aws.common.utils.KafkaJsonDeserializer;
import io.confluent.pie.csp_demo.aws.common.utils.KafkaJsonSerializer;
import io.kcache.KafkaCacheConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * SummaryCache is a Kafka-backed cache for Summary objects.
 */
@Component
@Slf4j
public class SummaryCache extends AbstractCache<Summary> {
    protected SummaryCache(KafkaCacheConfig cfg) {
        super(cfg, new KafkaJsonSerializer<>(), new KafkaJsonDeserializer<>(Summary.class), "chat_summary");
    }
}
