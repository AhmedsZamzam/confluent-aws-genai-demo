package io.confluent.pie.csp_demo.aws.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.pie.csp_demo.aws.common.models.Discussion;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Bedrock service
 */
@Component
@Slf4j
public class BedrockService {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {
    };

    private final static String SUMMARY_RESPONSE_KEY = "Here is the updated summary:";
    private final static String DEFAULT_SUMMARIZER_TEMPLATE = """
            Progressively summarize the lines of conversation provided, adding onto the previous summary returning a new summary.
            Make sure to specify if the loan application has been submitted for approval or not.

            EXAMPLE
            Current summary:
            The human asks what the Assistant thinks of artificial intelligence. The Assistant thinks artificial intelligence is a force for good.

            New lines of conversation:
            Human: Why do you think artificial intelligence is a force for good?
            Assistant: Because artificial intelligence will help humans reach their full potential.

            New summary:
            The human asks what the AI thinks of artificial intelligence. The AI thinks artificial intelligence is a force for good because it will help humans reach their full potential.
            END OF EXAMPLE

            Current summary:
            {summary}

            New lines of conversation:
            {new_lines}

            New summary:""";

    private final BedrockRuntimeClient client;

    @Value("${bedrock.modelId}")
    private String modelID;

    public BedrockService(@Autowired BedrockRuntimeClient client) {
        this.client = client;
    }

    /**
     * Send prompt to bedrock
     *
     * @param prompt             prompt
     * @param message            Message
     * @param inferenceParameter inference parameter
     * @return response
     */
    public Map<String, Object> chat(final String prompt, final String message, final BedrockInferenceParameter inferenceParameter) {
        return submit(prompt.replace("{input}", message), inferenceParameter);
    }

    /**
     * Send prompt to bedrock
     *
     * @param prompt  prompt
     * @param message Message
     * @return response
     */
    public Map<String, Object> chat(final String prompt, final String message) {
        return chat(prompt, message, new BedrockInferenceParameter());
    }

    /**
     * Send prompt to bedrock
     *
     * @param prompt         prompt
     * @param currentSummary current summary
     * @param discussion     discussion
     * @return response
     */
    public String summaries(final String prompt, final String currentSummary, final Discussion discussion) {
        return summaries(prompt, currentSummary, discussion, new BedrockInferenceParameter());
    }


    /**
     * Send prompt to bedrock
     *
     * @param prompt             prompt
     * @param currentSummary     current summary
     * @param discussion         discussion
     * @param inferenceParameter inference parameter
     * @return response
     */
    public String summaries(final String prompt,
                            final String currentSummary,
                            final Discussion discussion,
                            final BedrockInferenceParameter inferenceParameter) {
        final String promptToUse = (StringUtils.isEmpty(prompt)) ? DEFAULT_SUMMARIZER_TEMPLATE : prompt;
        final String parsedPrompt = promptToUse
                .replace("{summary}", StringUtils.isEmpty(currentSummary) ? "" : currentSummary)
                .replace("{new_lines}", discussion.toString());

        final Map<String, Object> summary = submit(parsedPrompt, inferenceParameter);
        if (summary.containsKey("completion")) {
            return summary
                    .get("completion")
                    .toString()
                    .substring(SUMMARY_RESPONSE_KEY.length() + 1)
                    .trim();
        }

        throw new RuntimeException("Unable to parse response from bedrock");
    }

    /**
     * Send prompt to bedrock
     *
     * @param prompt prompt
     * @return response
     */
    public Map<String, Object> submit(final String prompt) {
        return submit(prompt, new BedrockInferenceParameter());
    }

    /**
     * Send prompt to bedrock
     *
     * @param prompt             prompt
     * @param inferenceParameter inference parameter
     * @return response
     */
    public Map<String, Object> submit(final String prompt, final BedrockInferenceParameter inferenceParameter) {
        final String body = BedrockRequestBody.builder()
                .withModelId(modelID)
                .withPrompt(prompt)
                .withInferenceParameter("temperature", inferenceParameter.getTemperature())
                .withInferenceParameter("p", inferenceParameter.getP())
                .withInferenceParameter("k", inferenceParameter.getK())
                .withInferenceParameter("max_tokens", inferenceParameter.getMax_tokens())
                .build();

        // Invoke model
        InvokeModelRequest invokeModelRequest = InvokeModelRequest.builder()
                .modelId(modelID)
                .body(SdkBytes.fromString(body, Charset.defaultCharset()))
                .build();
        InvokeModelResponse invokeModelResponse = client.invokeModel(invokeModelRequest);
        JSONObject responseAsJson = new JSONObject(invokeModelResponse.body().asUtf8String());

        return responseAsJson.toMap();
    }

    public static Map<String, Object> extractJson(final Map<String, Object> response) throws JsonProcessingException {
        String json = response.get("completion").toString();
        final int start = json.indexOf("{");
        if (start == -1) {
            log.error("Error parsing json: {}", json);
            return null;
        }
        if (start > 0) {
            log.warn("Discarding {} characters", start);
            json = json.substring(start);
        }
        final int end = json.lastIndexOf("}");
        if (end == -1) {
            log.error("Error parsing json: {}", json);
            return null;
        }
        if (end < json.length() - 1) {
            log.warn("Discarding {} characters", json.length() - end - 1);
            json = json.substring(0, end + 1);
        }

        return objectMapper.readValue(json, typeRef);
    }
}
