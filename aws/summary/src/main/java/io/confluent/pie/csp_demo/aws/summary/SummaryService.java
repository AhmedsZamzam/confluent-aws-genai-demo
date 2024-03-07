package io.confluent.pie.csp_demo.aws.summary;

import io.confluent.pie.csp_demo.aws.common.caches.PromptCache;
import io.confluent.pie.csp_demo.aws.common.caches.SummaryCache;
import io.confluent.pie.csp_demo.aws.common.models.Discussion;
import io.confluent.pie.csp_demo.aws.common.models.Summary;
import io.confluent.pie.csp_demo.aws.common.utils.BedrockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class SummaryService {

    private final BedrockService bedrockService;
    private final SummaryCache summaryCache;

    private final PromptCache promptCache;

    public SummaryService(@Autowired BedrockService bedrockService,
                          @Autowired SummaryCache summaryCache,
                          @Autowired PromptCache promptCache) {
        this.bedrockService = bedrockService;
        this.summaryCache = summaryCache;
        this.promptCache = promptCache;
    }

    @KafkaListener(topics = "chat_discussion", groupId = "summary")
    public void onDiscussion(Discussion discussion) {
        log.info("Processing discussion: {}", discussion.getChat_input().getSession_id());

        final String sessionId = discussion.getChat_input().getSession_id();
        final String userId = discussion.getChat_output().getUser_id();
        if (StringUtils.isEmpty(userId)) {
            return;
        }

        final Summary userSummary = summaryCache.computeIfAbsent(userId, k -> new Summary(userId, sessionId));
        if (!StringUtils.equals(userSummary.getSession_id(), sessionId)) {
            log.info("New session: {}", sessionId);

            userSummary.setPrevious_summary(userSummary.getSummary());
            userSummary.setSession_id(sessionId);
            userSummary.setSummary("");
        }

        final String prompt = promptCache.get("summary");
        final String summary = bedrockService.summaries(prompt, userSummary.getSummary(), discussion);
        userSummary.setSummary(summary);

        // Force the cache update
        summaryCache.update(userId, new Summary(userSummary));

        log.info("Summary updated: {}", userSummary.getSession_id());
    }

}
