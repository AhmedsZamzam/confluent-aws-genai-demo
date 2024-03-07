package io.confluent.pie.csp_demo.aws.app.controller.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponse {
    private String error;
    private Map<String, Object> submission;

    public SubmissionResponse(String error) {
        this.error = error;
        this.submission = null;
    }

    public SubmissionResponse(Map<String, Object> submission) {
        this.error = null;
        this.submission = submission;
    }
}
