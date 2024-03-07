package io.confluent.pie.csp_demo.aws.setup.commands;

import io.confluent.pie.csp_demo.aws.common.caches.PromptCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class CreateData {

    private final PromptCache promptCache;

    public CreateData(@Autowired PromptCache promptCache) {
        this.promptCache = promptCache;
    }

    @ShellMethod(key = "prompt")
    public String createPrompt(@ShellOption String key, @ShellOption String prompt) {
        if (StringUtils.isEmpty(key)) {
            return "Key and prompt values are mandatory";
        }

        if (StringUtils.isEmpty(prompt)) {
            promptCache.put(key, null);
        } else {
            promptCache.put(key, prompt);
        }

        return "done.";
    }
}
