package io.confluent.pie.csp_demo.aws.app.controller.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientMessage {
    private String message;
    private String session_id;
    private String username;
}
