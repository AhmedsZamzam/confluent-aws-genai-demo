package io.confluent.pie.csp_demo.aws.app.controller.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Submission {

    private String submissionId;
    private String firstName;
    private String lastName;


    public static Submission newSubmission(final String submissionId, Map<String, Object> submission) {
        if (!submission.containsKey("firstName") || !submission.containsKey("lastName")) {
            log.warn("Submission {} does not contain firstName and lastName", submissionId);
            return null;
        }

        final String firstName = (String) submission.get("firstName");
        if (StringUtils.isEmpty(firstName)) {
            log.warn("Submission {} has blank firstName", submissionId);
            return null;
        }

        final String lastName = (String) submission.get("lastName");
        if (StringUtils.isEmpty(lastName)) {
            log.warn("Submission {} has blank lastName", submissionId);
            return null;
        }

        return new Submission(submissionId,
                (String) submission.get("firstName"),
                (String) submission.get("lastName"));
    }
}
