package io.confluent.pie.csp_demo.aws.common.configs;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

@Component
public class BedrockConfig {

    @Value("${bedrock.region}")
    private Region region;
    @Value("${bedrock.key}")
    private String key;
    @Value("${bedrock.secret}")
    private String secret;

    private BedrockRuntimeClient client;

    @PostConstruct
    public void init() {
        client = BedrockRuntimeClient.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(key, secret)))
                .build();
    }

    @PreDestroy
    public void destroy() {
        if (client != null) {
            client.close();
            client = null;
        }
    }

    @Bean
    public BedrockRuntimeClient getBedrock() {
        return client;
    }

}
