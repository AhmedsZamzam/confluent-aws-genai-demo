package io.confluent.pie.csp_demo.aws.app.controller.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String username;
    private String password;
}
