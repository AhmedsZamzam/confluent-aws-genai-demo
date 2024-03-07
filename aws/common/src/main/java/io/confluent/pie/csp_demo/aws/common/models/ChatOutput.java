package io.confluent.pie.csp_demo.aws.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatOutput {
    private String session_id;
    private String user_id;
    private String output;
}
