package io.confluent.pie.csp_demo.aws.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.pie.csp_demo.aws.common.utils.BedrockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Obfuscate the submission data
 */
@Component
@Slf4j
public class ObfuscationService {

    private final static String PROMPT = "##INSTRUCTIONS\n- De-identify the json document including date of birth and address.\n- Keep the first and last character, replace other characters with an X.\n\n";
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final BedrockService bedrockService;

    public ObfuscationService(@Autowired BedrockService bedrockService) {
        this.bedrockService = bedrockService;
    }

    /**
     * Obfuscate the submission data
     *
     * @param submission the submission data
     * @return the obfuscated submission data
     */
    public Map<String, Object> obfuscate(Map<String, Object> submission) {
        try {
            final String jsonSubmission = objectMapper.writeValueAsString(submission);
            final Map<String, Object> response = bedrockService.submit(PROMPT + jsonSubmission);
            return BedrockService.extractJson(response);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize submission", e);
            throw new RuntimeException("Failed to serialize submission", e);
        }
    }
}
