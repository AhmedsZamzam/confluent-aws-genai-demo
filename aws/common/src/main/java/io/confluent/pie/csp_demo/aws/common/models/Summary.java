package io.confluent.pie.csp_demo.aws.common.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Summary {

    private String previous_summary;
    private String summary;
    private String user_id;
    private String session_id;

    public Summary(final Summary summary) {
        this.previous_summary = summary.getPrevious_summary();
        this.summary = summary.getSummary();
        this.user_id = summary.getUser_id();
        this.session_id = summary.getSession_id();
    }

    public Summary(String user_id, String session_id) {
        this.user_id = user_id;
        this.session_id = session_id;
        this.summary = "";
        this.previous_summary = "";
    }
}
