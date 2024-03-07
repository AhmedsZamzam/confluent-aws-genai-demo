package io.confluent.pie.csp_demo.aws.bot.caches;

import io.confluent.pie.csp_demo.aws.common.caches.AbstractCache;
import io.confluent.pie.csp_demo.aws.common.models.UserSummary;
import io.confluent.pie.csp_demo.aws.common.utils.KafkaJsonDeserializer;
import io.confluent.pie.csp_demo.aws.common.utils.KafkaJsonSerializer;
import io.kcache.KafkaCacheConfig;
import org.springframework.stereotype.Component;

/**
 * UserSummaryCache is a Kafka-backed cache for UserSummary objects.
 */
@Component
public class UserSummaryCache extends AbstractCache<UserSummary> {
    protected UserSummaryCache(KafkaCacheConfig cfg) {
        super(cfg, new KafkaJsonSerializer<>(), new KafkaJsonDeserializer<>(UserSummary.class), "user_history");
    }
}
