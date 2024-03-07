package io.confluent.pie.csp_demo.aws.bot.caches;

import io.confluent.pie.csp_demo.aws.common.caches.AbstractCache;
import io.confluent.pie.csp_demo.aws.common.models.User;
import io.confluent.pie.csp_demo.aws.common.utils.KafkaJsonDeserializer;
import io.confluent.pie.csp_demo.aws.common.utils.KafkaJsonSerializer;
import io.kcache.KafkaCacheConfig;
import org.springframework.stereotype.Component;

/**
 * UserCache is a Kafka-backed cache for User objects.
 */
@Component
public class UserCache extends AbstractCache<User> {
    protected UserCache(KafkaCacheConfig cfg) {
        super(cfg, new KafkaJsonSerializer<>(), new KafkaJsonDeserializer<>(User.class), "users");
    }
}
