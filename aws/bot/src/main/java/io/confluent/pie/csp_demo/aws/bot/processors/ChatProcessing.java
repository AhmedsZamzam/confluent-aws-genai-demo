package io.confluent.pie.csp_demo.aws.bot.processors;

import io.confluent.pie.csp_demo.aws.bot.PromptProvider;
import io.confluent.pie.csp_demo.aws.common.caches.DiscussionsCache;
import io.confluent.pie.csp_demo.aws.common.models.ChatInput;
import io.confluent.pie.csp_demo.aws.common.models.ChatOutput;
import io.confluent.pie.csp_demo.aws.common.models.Discussion;
import io.confluent.pie.csp_demo.aws.common.models.Discussions;
import io.confluent.pie.csp_demo.aws.common.utils.BedrockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;

import java.util.Map;
import java.util.Objects;

/**
 * Chat processor
 */
@Slf4j
public class ChatProcessing implements Processor<String, ChatInput, String, Discussion> {

    private final PromptProvider promptProvider;
    private final BedrockService bedrockService;
    private final DiscussionsCache discussionsCache;
    private ProcessorContext<String, Discussion> context;

    public ChatProcessing(PromptProvider promptProvider,
                          BedrockService bedrockService,
                          DiscussionsCache discussionsCache) {
        this.promptProvider = promptProvider;
        this.bedrockService = bedrockService;
        this.discussionsCache = discussionsCache;
    }

    @Override
    public void init(ProcessorContext<String, Discussion> context) {
        this.context = context;
    }

    @Override
    public void process(Record<String, ChatInput> record) {
        log.info("Processing chat input: {}", record.value().getSession_id());
        final ChatInput chatInput = record.value();
        final String prompt = promptProvider.getPrompt(chatInput.getUser_id(), chatInput.getSession_id());
        final String discussionsKey = chatInput.getUser_id();
        if (StringUtils.isEmpty(discussionsKey)) {
            log.error("No user id...");
            return;
        }

        Discussions discussions = discussionsCache.containsKey(discussionsKey)
                ? discussionsCache.get(discussionsKey)
                : new Discussions(discussionsKey, chatInput.getSession_id());
        if (!StringUtils.equals(discussions.getSessionId(), chatInput.getSession_id())) {
            // New session, new discussions
            discussions = new Discussions(discussionsKey, chatInput.getSession_id());
        }

        final String promptWithDiscussion = prompt.replace("{history}", discussions.toString());

        final Map<String, Object> response = bedrockService.chat(promptWithDiscussion, chatInput.getInput());
        final String output = (String) response.get("completion");
        final ChatOutput chatOutput = new ChatOutput(chatInput.getSession_id(), chatInput.getUser_id(), output);
        final Discussion discussion = new Discussion(chatInput, chatOutput);
        discussions.add(discussion);

        // Update the discussion
        discussionsCache.put(discussionsKey, discussions);

        // Forward
        context.forward(new Record<>(chatInput.getSession_id(), discussion, record.timestamp()));
        log.info("Chat output forwarded: {}", chatOutput.getSession_id());
    }
}
