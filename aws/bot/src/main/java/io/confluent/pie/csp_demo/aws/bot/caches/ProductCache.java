package io.confluent.pie.csp_demo.aws.bot.caches;

import io.confluent.pie.csp_demo.aws.common.caches.AbstractCache;
import io.confluent.pie.csp_demo.aws.common.models.Product;
import io.confluent.pie.csp_demo.aws.common.utils.KafkaJsonDeserializer;
import io.confluent.pie.csp_demo.aws.common.utils.KafkaJsonSerializer;
import io.kcache.KafkaCacheConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Product cache
 */
@Component
public class ProductCache extends AbstractCache<Product> {

    public ProductCache(@Autowired KafkaCacheConfig cfg) {
        super(cfg,
                new KafkaJsonSerializer<>(),
                new KafkaJsonDeserializer<>(Product.class),
                "products");
    }

}
