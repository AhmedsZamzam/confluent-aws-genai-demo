package io.confluent.pie.csp_demo.aws.bot;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.confluent.pie.csp_demo.aws.bot.caches.*;
import io.confluent.pie.csp_demo.aws.common.caches.PromptCache;
import io.confluent.pie.csp_demo.aws.common.caches.SummaryCache;
import io.confluent.pie.csp_demo.aws.common.models.Summary;
import io.confluent.pie.csp_demo.aws.common.models.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class PromptProvider implements CacheLoader<Pair<String, Boolean>, String> {

    private final ProductCache productCache;
    private final PromptCache promptCache;
    private final SummaryCache summaryCache;
    private final UserSummaryCache userSummaryCache;
    private final UserCache userCache;

    private final LoadingCache<Pair<String, Boolean>, String> cache = Caffeine.newBuilder().build(this);


    public PromptProvider(@Autowired ProductCache productCache,
                          @Autowired PromptCache promptCache,
                          @Autowired SummaryCache summaryCache,
                          @Autowired UserSummaryCache userSummaryCache,
                          @Autowired UserCache userCache) {
        this.productCache = productCache;
        this.promptCache = promptCache;
        this.summaryCache = summaryCache;
        this.userSummaryCache = userSummaryCache;
        this.userCache = userCache;

        this.summaryCache.setCacheUpdateHandler((key, value, oldValue) -> {
            if (value != null) {
                final Pair<String, Boolean> cacheKey = Pair.of(key, true);
                if (oldValue != null && !StringUtils.equals(value.getSession_id(), oldValue.getSession_id())) {
                    cache.invalidate(cacheKey);
                }
            }
        });
        this.promptCache.setCacheUpdateHandler((key, value, oldValue) -> {
            cache.invalidateAll();
        });
    }

    /**
     * Get prompt
     *
     * @param userId    User ID
     * @param sessionId Session ID
     * @return Prompt
     */
    public String getPrompt(final String userId, final String sessionId) {
        final String id = StringUtils.isNotEmpty(userId) ? userId : sessionId;
        final boolean isCustomer = StringUtils.isNotEmpty(userId) && userCache.containsKey(userId);
        return cache.get(Pair.of(id, isCustomer));
    }

    /**
     * Load prompt
     *
     * @param key Key
     * @return Prompt
     * @throws Exception Exception
     */
    public String load(Pair<String, Boolean> key) throws Exception {
        final String id = key.getLeft();
        final boolean isCustomer = key.getRight();
        final boolean alreadyCustomer = isCustomer && StringUtils.isNotEmpty(id);

        // Prompt
        final String prompt;
        if (alreadyCustomer && promptCache.containsKey(id)) {
            prompt = promptCache.get(id);
        } else if (!alreadyCustomer) {
            prompt = promptCache.get("default");
        } else {
            prompt = promptCache.get("existed_customer");
        }

        // Products
        final String products = productCache.getAll()
                .stream()
                .filter(p -> StringUtils.isNumeric(p.getKey()))
                .map(Map.Entry::getValue)
                .map(p -> String.format("- The ** %s ** is a %s", ordinal(Integer.parseInt(p.getProduct_id())), p.getDescription()))
                .reduce((a, b) -> a + "\n\n" + b)
                .orElse("");

        // Previous conversation summary
        final String summary;
        if (summaryCache.containsKey(id)) {
            final Summary userSummary = summaryCache.get(id);
            summary = (StringUtils.isNotEmpty(userSummary.getPrevious_summary()))
                    ? "Here is a summary of the previous conversation:\n\n" + summaryCache.get(id).getSummary()
                    : "";
        } else {
            summary = "";
        }

        String parsedPrompt = prompt;

        // User info
        if (alreadyCustomer && userCache.containsKey(id)) {
            final User user = userCache.get(id);
            parsedPrompt = parsedPrompt
                    .replace("{Firstname}", user.getFirst_name())
                    .replace("{Lastname}", user.getLast_name());
        } else {
            parsedPrompt = parsedPrompt
                    .replace("{Firstname}", "")
                    .replace("{Lastname}", "");
        }

        // User history with the bank
        final String userHistory;
        if (alreadyCustomer && userSummaryCache.containsKey(id)) {
            userHistory = String.format("Here's a few data that Colin has retrieved about his customers:\n\n%s", userSummaryCache.get(id));
        } else {
            userHistory = "";
        }

        return parsedPrompt
                .replace("{products}", products)
                .replace("{summary}", summary)
                .replace("{user_history}", userHistory);
    }

    /**
     * Ordinal
     *
     * @param i Number
     * @return Ordinal
     */
    public static String ordinal(int i) {
        String[] suffixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        return switch (i % 100) {
            case 11, 12, 13 -> i + "th";
            default -> i + suffixes[i % 10];
        };
    }
}
