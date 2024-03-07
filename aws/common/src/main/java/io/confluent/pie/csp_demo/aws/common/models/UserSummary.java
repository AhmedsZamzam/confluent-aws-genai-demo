package io.confluent.pie.csp_demo.aws.common.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSummary {
    private String user_id;
    private String history;
}
