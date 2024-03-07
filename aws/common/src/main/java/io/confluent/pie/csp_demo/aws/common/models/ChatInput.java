package io.confluent.pie.csp_demo.aws.common.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ChatInput {
    private String session_id;
    private String user_id;
    private String input;
    private String datetime;
}
