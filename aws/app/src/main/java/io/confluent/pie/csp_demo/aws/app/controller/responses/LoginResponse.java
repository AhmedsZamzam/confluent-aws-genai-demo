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
public class LoginResponse {
    private String type;
    private boolean status;
    private String message;
    private Map<String, Object> data;
}
