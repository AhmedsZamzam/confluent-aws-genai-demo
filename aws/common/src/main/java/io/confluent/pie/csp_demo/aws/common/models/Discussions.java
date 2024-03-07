package io.confluent.pie.csp_demo.aws.common.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Discussions
 */
@Getter
@Setter
public class Discussions {
    private final List<Discussion> discussions = new ArrayList<>();
    private final String sessionId;
    private final String userId;

    public Discussions(String userId, String sessionId) {
        this.userId = userId;
        this.sessionId = sessionId;
    }

    public Discussions(Discussions discussions) {
        this.userId = discussions.userId;
        this.sessionId = discussions.sessionId;
        this.discussions.addAll(discussions.discussions);
    }

    public Discussions() {
        this.userId = "";
        this.sessionId = "";
    }

    public void add(Discussion discussion) {
        discussions.add(discussion);
    }

    @JsonIgnore
    public Discussion getLastEntry() {
        return discussions.get(discussions.size() - 1);
    }

    @Override
    public String toString() {
        return discussions.stream()
                .map(discussion -> String.format("Human: %s\nAssistant:%s", discussion.getChat_input().getInput(), discussion.getChat_output().getOutput()))
                .reduce((s, s2) -> s + "\n" + s2)
                .orElse("");
    }

}
