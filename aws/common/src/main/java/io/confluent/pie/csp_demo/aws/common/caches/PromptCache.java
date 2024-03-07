package io.confluent.pie.csp_demo.aws.common.caches;

import io.confluent.pie.csp_demo.aws.common.caches.AbstractCache;
import io.kcache.KafkaCacheConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Prompt cache
 */
@Component
public class PromptCache extends AbstractCache<String> {

    public PromptCache(@Autowired KafkaCacheConfig cfg) {
        super(cfg,
                new StringSerializer(),
                new StringDeserializer(),
                "prompts");
    }
}