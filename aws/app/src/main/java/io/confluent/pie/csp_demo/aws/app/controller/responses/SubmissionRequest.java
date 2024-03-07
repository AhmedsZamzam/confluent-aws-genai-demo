package io.confluent.pie.csp_demo.aws.app.controller.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmissionRequest {
    private String submissionId;
    private String userId;
}
