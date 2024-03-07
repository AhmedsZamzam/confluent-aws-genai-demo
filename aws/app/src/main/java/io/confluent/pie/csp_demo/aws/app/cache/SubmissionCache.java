package io.confluent.pie.csp_demo.aws.app.cache;

import io.confluent.pie.csp_demo.aws.common.caches.AbstractCache;
import io.confluent.pie.csp_demo.aws.common.models.GenericDeserializer;
import io.confluent.pie.csp_demo.aws.common.models.GenericSerializer;
import io.kcache.KafkaCacheConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SubmissionCache extends AbstractCache<Map<String, Object>> {
    protected SubmissionCache(@Autowired KafkaCacheConfig cfg) {
        super(cfg, new GenericSerializer(), new GenericDeserializer(), "submit");
    }
}
