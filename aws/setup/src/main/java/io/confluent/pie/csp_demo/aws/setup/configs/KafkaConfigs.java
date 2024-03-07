package io.confluent.pie.csp_demo.aws.setup.configs;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;

public class KafkaConfigs {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${spring.kafka.security.protocol}")
    private String securityProtocol;

    @Value(value = "${spring.kafka.sasl.mechanism}")
    private String saslMechanism;

    @Value(value = "${spring.kafka.sasl.jaas.config}")
    private String jaasConfig;

    @Value(value = "${spring.kafka.application-id}")
    private String applicationId;

    @Value(value = "${spring.kafka.client-id}")
    private String clientId;

    @Value(value = "${spring.kafka.group-id}")
    private String groupId;

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
        props.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);
        props.put(SECURITY_PROTOCOL_CONFIG, securityProtocol);

        return new KafkaAdmin(props);
    }
}
