package io.confluent.pie.csp_demo.aws.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Discussion {
    private ChatInput chat_input;
    private ChatOutput chat_output;

    @Override
    public String toString() {
        return String.format("Human: %s\nAssistant:%s", chat_input.getInput(), chat_output.getOutput());
    }
}
