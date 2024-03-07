package io.confluent.pie.csp_demo.aws.app.controller.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServerResponse {
    private String data;
    private String session_id;
}
