package io.confluent.pie.csp_demo.aws.submit;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.confluent.pie.csp_demo.aws.common.caches.AbstractCache;
import io.confluent.pie.csp_demo.aws.common.caches.DiscussionsCache;
import io.confluent.pie.csp_demo.aws.common.models.Discussion;
import io.confluent.pie.csp_demo.aws.common.models.Discussions;
import io.confluent.pie.csp_demo.aws.common.utils.BedrockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class SubmissionService implements AbstractCache.CacheUpdateHandler<Discussions> {

    private final static String PROMPT = """
            Extract mandatory information from the conversation in json format.
                        
            ### Instructions
            - The required information are First name, Last Name, Address, Date of Birth, Citizenship, Credit Score, SSN and selected product index.
            - Return only the json result
                                      
            ### Conversation
            {conversation}""";

    private final DiscussionsCache discussionsCache;
    private final BedrockService bedrockService;

    private KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    public SubmissionService(@Autowired DiscussionsCache discussionsCache,
                             @Autowired BedrockService bedrockService,
                             @Autowired KafkaTemplate<String, Map<String, Object>> kafkaTemplate) {
        this.discussionsCache = discussionsCache;
        this.discussionsCache.setCacheUpdateHandler(this);
        this.bedrockService = bedrockService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void handleUpdate(String key, Discussions value, Discussions oldValue) {
        final String newSessionId = (value != null) ? value.getSessionId() : null;
        final String oldSessionId = (oldValue != null) ? oldValue.getSessionId() : null;

        final Discussion lastDiscussion;
        if (!StringUtils.equals(newSessionId, oldSessionId) && oldValue != null) {
            lastDiscussion = oldValue.getLastEntry();
        } else if (value != null) {
            lastDiscussion = value.getLastEntry();
        } else {
            log.error("No discussions...");
            return;
        }

        final String lastResponse = lastDiscussion.getChat_output().getOutput();
        if (!StringUtils.isEmpty(lastResponse) && lastResponse.endsWith("#EOF#") && value != null) {
            final String prompt = PROMPT.replace("{conversation}", value.toString());
            final Map<String, Object> information = bedrockService.submit(prompt);
            try {
                final Map<String, Object> submission = BedrockService.extractJson(information);
                kafkaTemplate.send("submit", value.getUserId() + value.getSessionId(), submission);
            } catch (JsonProcessingException e) {
                log.error("Error parsing json.", e);
            }
        }
    }
}
