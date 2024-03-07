package io.confluent.pie.csp_demo.aws.common.configs;

import io.kcache.KafkaCacheConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KCacheConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${spring.kafka.security.protocol}")
    private String securityProtocol;

    @Value(value = "${spring.kafka.sasl.mechanism}")
    private String saslMechanism;

    @Value(value = "${spring.kafka.sasl.jaas.config}")
    private String jaasConfig;

    @Value(value = "${spring.kafka.client-id}")
    private String clientId;

    @Value(value = "${spring.kafka.group-id}")
    private String groupId;

    @Bean
    KafkaCacheConfig kCacheStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(KafkaCacheConfig.KAFKACACHE_GROUP_ID_CONFIG, "cache-" + groupId);
        props.put(KafkaCacheConfig.KAFKACACHE_CLIENT_ID_CONFIG, "cache-" + clientId);
        props.put(KafkaCacheConfig.KAFKACACHE_BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(KafkaCacheConfig.KAFKACACHE_SECURITY_PROTOCOL_CONFIG, securityProtocol);
        props.put("kafkacache." + SaslConfigs.SASL_MECHANISM, saslMechanism);
        props.put(KafkaCacheConfig.KAFKACACHE_SASL_JAAS_CONFIG_CONFIG, jaasConfig);

        return new KafkaCacheConfig(props);
    }
}
